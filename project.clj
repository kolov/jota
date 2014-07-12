(defproject jota "0.2.0"
  :description "light loggin for clojure"
  :url "http://github.com/kolov/jota"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {
              :dev {:dependencies [[midje "1.6.3"]
                                   [midje-junit-formatter "0.1.0-SNAPSHOT"]]
                    :plugins      [[lein-deps-tree "0.1.2"]
                                   [lein-midje "3.0.0"]
                                   [test2junit "1.0.1"]
                                   [lein-release "1.0.5"]]
                    }
              }
  )
