# Kermoss: A Reactive Business Flow
![KERMOSS-LOGO](reactive-business-flow/src/docs/asciidoc/images/KERMOSS-LOGO.png)

[![Build Status](https://travis-ci.org/kermoss/kermoss.svg?branch=master)](https://travis-ci.org/kermoss/kermoss)
[![codecov](https://codecov.io/gh/kermoss/kermoss/branch/master/graph/badge.svg)](https://codecov.io/gh/kermoss/kermoss)
![GitHub](https://img.shields.io/github/license/kermoss/kermoss.svg)
![Maven Central](https://img.shields.io/maven-central/v/io.kermoss/reactive-business-flow.svg)

## Problem:
  > To improve performance and consistency in a distributed environment, an application shouldn't go for ways such as two-phase commit or you'll find your self in a situation similar to the two generals problem, instead the application should strive for eventual consistency. in such a model a transactional operation is devided into multiple steps. during the execution.


## Kermoss ToolBox:

- Saga of transactions distributed via federated orchestrators (realm-ambassador)
- Idempotence out of box at all levels, out of the box
- Very advanced auditabilty: Debugging and monitoring of the entire value chain managed by Kermoss
- Reliability is the core principle of Kermoss and the dimension that gave it more chance to exist

See [the reference documentation!](https://kermoss.github.io/) for more information .

Acknowledgements
=================
* YourKit supports Kermoss with its full-featured Java Profiler. Take a look at YourKit's leading software products: <a href="https://www.yourkit.com/java/profiler/features/">YourKit Java Profiler</a>.
<img src="https://www.yourkit.com/images/yklogo.png" title="YourKit">