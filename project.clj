(defproject lojbar "0.1.0-SNAPSHOT"
  :description "Atomic lemonbar module creation in Clojure"
  :url "http://example.com/FIXME"
  :license {:name "BSD 2-Clause License"
            :url "https://opensource.org/licenses/BSD-2-Clause"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :main ^:skip-aot lojbar.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
