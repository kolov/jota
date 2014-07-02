(ns jota.core
  (:require [clojure.java.io :as io])
  )


(def logconfig (atom nil))
(def levels [:trace :debug :info :warn :error])

(defn category [x]
  "Make a keyword from everything"
  (cond
    (= (class x) clojure.lang.Namespace) (keyword (.name x))
    (= (class x) clojure.lang.Keyword) x

    :default (keyword (str x))))

(defmacro getns [] (eval *ns*))
(defn check-argument
  ([a txt] (if (not a) (throw (IllegalArgumentException. txt))))
  ([a] (check-argument a "error")))

(defn add-val [curval newval] "sets a value or adds it to a set"
  (cond
    (nil? curval) newval
    (coll? curval) (conj curval newval)
    :default #{curval newval}
    )
  )
(defn- set-attr! [x attr val] (swap! logconfig assoc-in [(category x) attr] val))
(defn- add-attr! [x attr val] (swap! logconfig update-in [(category x) attr] add-val val))
(defn set-level! [x level] (check-argument (some #{level} levels)) (set-attr! x :level level))
(defn set-writer! [x writer] (set-attr! x :writer writer))
(defn add-writer! [x writer] (add-attr! x :writer writer))

(defn- get-attr [x attr]
  (if-let [found (get-in @logconfig [(category x) attr])]
    found (get-in @logconfig [:root attr])))
(defn get-level [x] (get-attr x :level))
(defn get-writer [x] (get-attr x :writer))

(defn reset-log! [] (reset! logconfig {:root {:level :trace :writer println}}))
(reset-log!)

(defn passes [level configured]
  (let [order levels
        ix-level (.indexOf order level)
        ix-configured (.indexOf order configured)
        ]
    (<= ix-configured ix-level))
  )

(defn log? [x level] (passes level (get-level x)))

(defn log-message [writers msg]
  (cond (set? writers) (doseq [w writers] (w msg))
        (fn? writers) (writers msg)
        :default (throw (Exception. "Set or function expected"))
        ))
(defn logprint [x level txt]
  (let [cat (category x)]
    (if (log? cat level)
      (log-message (get-writer cat) (str (name cat) ":" (name level) ": " txt)))))

(defmacro dolog [level & args]
  `(logprint (getns) ~level (apply str (vector ~@args))))

(defmacro trace [& args] `(dolog :trace ~@args))
(defmacro debug [& args] `(dolog :debug ~@args))
(defmacro info [& args] `(dolog :info ~@args))
(defmacro warn [& args] `(dolog :warn ~@args))
(defmacro error [& args] `(dolog :error ~@args))

(defn read-config [r]
  (read-string (slurp (io/resource r)))
  )
