# Calling URL to put in cache
## Stream

```
                  +-------------+
                +-------------+ |
+--------+    +-------------+ | |    +--------------+
|        |    |           | | | |    |              |
|  Load  +---->  Request  | | +-+---->    Report    |
|  URL   |    |    URL    | +-+      | cache status |
|        |    |           +-+        |              |
+--------+    +-----------+          +--------------+
```

## Components
### Load URL
**Load URL** is a source that feed with absolute URL. This source can be anything that implement **URLFeeder** trait.

### Request URL
**Request URL** is any HTTP Client. This stage is described by the trait **HttpClient** and any of those can be used.

### Report cache status
**Report cache status** is an aggregator that report what happened for each URL requests. It is described by **Reporter** trait.

## Assembly
In order to run this flow, **CacheProcessor** assemble all those components and run it.