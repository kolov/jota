Jota is a inimal log system for Clojure

# Intro

After ```(require [jota.core :as log]```:
```clojure
(ns myapp.core)

(log/debug "Value=" 42)
(log/error "Value=" 43)
```
will output:
```clojure
myapp.core:debug: Value=42
myapp.core:error: Value=43
```

To suppress the debug-level output: ```(set-level! 'myapp.core :error)``` or ```(set-level! :root :error)``` 

## Levels
Jota supports ```:trace, :debug, :info, :warn``` and ```:error```, with the correspondign functions ```log/trace, log/debug``` etc.

## Writers
The standard writer is ```println```. You can set or add a writer:
```clojure
   (set-writer! 'myapp.core (fn[x] (print x)))
   (add-writer! :root (fn[x] (print x)))
```
TODO: use logback appenders

# Settings
Jota will search the level and writer for the namespace where ```log``` is called. If not found, it will fall back to the settings to root. When setting sevels or writers, you can use namespaces, keywords or symbol, all are mormalized to keywords. THe following three are equivalent:

```clojure
(set-level! *ns* :error)
(set-level! 'myapp.core :error)
(set-level! :myapp.core :error)
(set-level! "myapp.core" :error)
```

On startup, Jota will search jota-setting-test.clj and jota-setting.clj and will print the file path from which it initialized:

    jota initialized from  /Users/kolov/projects/jota/test/test-config.clj
    
A sample initializatin file:
```clojure
{:root           {:level :info}
 :myapp.core     {:level :debug :writer print}
}
```


