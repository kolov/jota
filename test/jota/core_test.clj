(ns jota.core-test
  (:require [clojure.test :refer :all]
            [jota.core :refer :all]
            [midje.sweet :refer :all]))

(fact "add-val"
      (add-val nil 1) => 1
      (add-val 2 1) => #{1 2}
      (add-val #{2 3} 1) => #{1 2 3}
      )
(fact
  (category *ns*) => :jota.core-test
  (category 'ala.bala) => :ala.bala
  (category :ala.bala) => :ala.bala
  )

(fact
  (passes :info :info) => true
  (passes :trace :info) => false
  (passes :error :info) => true
  )

(fact
  "Initial configuration contains root level, writer"
  (let [dummy (reset-log!)]
    (keys @logconfig) => [:root]
    (keys (:root @logconfig)) => [:level :writer]
    ))

(fact
  "Setting level en writers ok"
  (let [dummy (reset-log!)
        some-fn1 (fn [x] 1)
        some-fn2 (fn [x] 2)]
    (do (set-level! "x" :error) (get-level "x")) => :error
    (do (set-level! "x" :trace) (get-level "x")) => :trace
    (do (set-writer! "x" some-fn1) (get-writer "x")) => some-fn1
    (do (set-writer! "x" some-fn2) (get-writer "x")) => some-fn2
    (do (set-writer! "x" some-fn1) ((get-writer "x") 0)) => 1
    ))

(fact
  "Adding writers ok"
  (let [dummy (reset-log!)
        some-fn1 (fn [x] 1)
        some-fn2 (fn [x] 2)]
    (do (set-writer! "x" some-fn1) (get-writer "x")) => some-fn1
    (do (add-writer! "x" some-fn2) (get-writer "x")) => #{some-fn1 some-fn2}
    (do (set-writer! "x" some-fn1) (get-writer "x")) => some-fn1
    (do (add-writer! "x" some-fn2) (get-writer "x")) => #{some-fn1 some-fn2}
    (do (add-writer! "x" some-fn2) (get-writer "x")) => #{some-fn1 some-fn2}
    ))

(fact "setting wrong level causes error"
      (set-level! "x" :trace) => anything
      (set-level! "x" :bad) => (throws Exception)
      )

(fact
  "levels and writer honoured"
  (let [dummy (reset-log!)
        dummy (set-level! :root :debug)
        dummy (set-writer! :root (fn [x] (print (str "r:" x))))
        dummy (set-level! "x" :info)
        dummy (set-writer! "x" (fn [x] (print (str "x:" x))))
        dummy (set-level! "y" :warn)
        dummy (set-writer! "y" (fn [x] (print (str "y:" x))))
        ]

    (with-out-str (logprint "undefined" :trace "11")) => ""
    (with-out-str (logprint "undefined" :debug "11")) => "r:undefined:debug: 11"

    (with-out-str (logprint "x" :trace "11")) => ""
    (with-out-str (logprint "x" :info "11")) => "x:x:info: 11"
    (with-out-str (logprint "x" :warn "11")) => "x:x:warn: 11"
    (with-out-str (logprint "x" :error "11")) => "x:x:error: 11"

    (with-out-str (logprint "y" :trace "11")) => ""
    (with-out-str (logprint "y" :info "11")) => ""
    (with-out-str (logprint "y" :warn "11")) => "y:y:warn: 11"
    (with-out-str (logprint "y" :error "11")) => "y:y:error: 11"

    ))



(fact
  "multiple writers honoured"
  (let [dummy (reset-log!)
        dummy (set-level! :root :debug)
        dummy (set-writer! :root (fn [x] (print (str "r:" x))))

        dummy (set-writer! "z" (fn [x] (print (str "z1:" x))))
        dummy (add-writer! "z" (fn [x] (print (str "z2:" x))))
        ]

    (with-out-str (logprint "z" :error "11")) => "z1:z:error: 11z2:z:error: 11"

    ))

(fact "Reading configuration"
      (read-config "test-config.clj") => {
                                           :root
                                                           {
                                                             :level :info
                                                             }

                                           :jota.core_test {:level :debug :writer #{}
                                           }}
      )


