# User Guide

## Terminology

### GlobalTransactionWorker
it describes when a `GlobalTransaction` starts and when it ends.

### LocalTransactionWorker
it describes when a `LocalTransaction` starts and when it should move to the next `LocalTransaction`.

## Configuration

### DecoderRegistry

Provide a `Bean` that extends `DecoderRegistry`

```java
@Component
public class PizzashopDecoderRegistry extends DecoderRegistry {

    @PostConstruct
    public void setup() {
        this.put("pay-delivery-service", new PayDeliveryServiceDecoder();
        this.put("deliver-pizza", new DeliverPizzaDecoder();
        this.put("call-market", new CallDecoder());
    }
}
```


### Define your BFM

depending on your use case you'll break down your workflow or your business logic which will be managed by a `GlobalTransaction` into smaller pieces that will each be managed by a `LocalTransactionWorker` 


#### OrderPizzaStateWorker

in this example we'll be Cooking pizza, but before doing anything we'll need the ingredients

```java
@Component
public class OrderPizzaStateWorker extends GlobalTransactionWorker<OrderPizzaReceivedEvent, OrderPizzaReadyEvent> {
    @Autowired
    PizzaService pizzaService;

    // define What State is this worker handling
    public OrderPizzaStateWorker() {
        super(new WorkerMeta("OrderPizzaService"));
    }

    @Override
    @BusinessGlobalTransactional
    public GlobalTransactionStepDefinition onStart(OrderPizzaReceivedEvent orderPizzaReceivedEvent) {
        Need need = new Need(Arrays.asList("Tomatos", "mozzarella cheese", "meat"));
        CallMarketCommand cmd = new CallMarketCommand("call-market", "",
                need, "market-service");
       return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReceivedEvent)
                .meta(this.meta)// do not forget this, kermoss needs to know the metadata of the TransactionWorker
                .process(Optional.empty())
                .prepare(Stream.of(cmd))// fire off call-market command to market-service
                .blow(Stream.of(new OrderPizzaPendingEvent(orderPizzaReceivedEvent.getCartId())))// trigger the next TransactionWorker that is waiting for a OrderPizzaPendingEvent
                .build();
    }

    @Override
    @CommitBusinessGlobalTransactional
    public GlobalTransactionStepDefinition onComplete(OrderPizzaReadyEvent orderPizzaReadyEvent) {
        return GlobalTransactionStepDefinition.builder()
                .in(orderPizzaReadyEvent)
                .meta(this.meta)
                .blow(Stream.of(new OrderPizzaReadyForShipmentEvent()))// mark the GTX as COMPLETE, probably start another GTX if a GlobalTransactionWorker is listening for OrderPizzaReadyForShipmentEvent
                .process(Optional.empty())
                .build();
    }
}
```

#### ChefCookingServiceWorker

we can't make no Pizza without a Chef, can we?

```java
@Component
public class ChefCookingServiceWorker extends LocalTransactionWorker<OrderPizzaPendingEvent, OrderPizzaCookedEvent> {
    @Autowired
    private PizzaService pizzaService;

    public ChefCookingServiceWorker() {
        super(new WorkerMeta("MakingPizza", "OrderPizzaService"));
    }

    @Override
    @BusinessLocalTransactional
    public LocalTransactionStepDefinition onStart(OrderPizzaPendingEvent orderPizzaPendingEvent) {
        return LocalTransactionStepDefinition.builder()
                .in(orderPizzaPendingEvent)
                .process(makePizzaSupplier(orderPizzaPendingEvent.getCartID()))
                .blow(Stream.of(new BakingPizzaPendingEvent(orderPizzaPendingEvent.getCartID())))
                .meta(this.meta)
                .build();
    }

    @Override
    @SwitchBusinessLocalTransactional
    public LocalTransactionStepDefinition onNext(OrderPizzaCookedEvent orderPizzaCookedEvent) {
        ToDeliver b = new ToDeliver("Some address", pizzaService.getOrders().get(orderPizzaCookedEvent.getCartId()).getPizzas()
                .stream().map(Pizza::getName)
                .collect(Collectors.toList()));
        OrderPizzaReadyForShipmentCommand cmd = new OrderPizzaReadyForShipmentCommand("deliver-pizza", "",
                b, "shippement-service");
        return LocalTransactionStepDefinition.builder()
                .in(orderPizzaCookedEvent)
                .process(makePizzaReadySupplier(orderPizzaCookedEvent.getCartId()))
                .blow(Stream.of(new OrderPizzaReadyEvent(orderPizzaCookedEvent.getCartId())))
                .send(Stream.of(cmd))
                .meta(this.meta)
                .build();
    }

    private Optional<Supplier> makePizzaSupplier(String id){
        log.info("Hey, I'll start cooking your pizza {} right now! ", id);

        Supplier s = () -> {
            pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
                p.setPizzaState(Pizza.PizzaState.AJINA);
                log.info("You pizza status is {}", p.getPizzaState());
            });
            return null;
        };
        return Optional.of(s);
    }

    private Optional<Supplier> makePizzaReadySupplier(String id){
        log.info("Hey, your pizza {} is ready! ", id);

        Supplier s = () -> {
            pizzaService.getOrders().get(id).getPizzas().stream().forEach(p -> {
                p.setPizzaState(Pizza.PizzaState.WAJDAT);
                log.info("You pizza status is {}", p.getPizzaState());

            });
            return null;
        };
        return Optional.of(s);
    }
}
```