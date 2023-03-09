(ns tc-md-example
  (:require
   [nextjournal.clerk :as clerk]
   [tech.v3.dataset.print :as p]
   [tablecloth.api :as tc]))

;;; # Markdown Tables

;; You can create markdown tables by concating some strings together like
;; so.
;;
;; The syntax for markdown tables is [here](https://www.markdownguide.org/extended-syntax/#tables).
;;
(clerk/md
 (str
  "| Foo | Bar | Baz |\n"
  "| :--- | :---: | ---: |\n"
  "| foo | bar | 12.00 |\n"
  "| quz | quux | 1.0 |\n"))


;; This means you can also get data out of a tablecloth/tech.ml dataset
;; and format it too.
(defn md-row
  [v]
  (str "| " (apply str (interpose " | " v)) " |\n"))


(defn md-table
  [{:keys [columns alignment-spec data]}]
  (clerk/md
   (apply
    str
    (into [(md-row columns)
           (md-row alignment-spec)]
          (comp
           (map
            (fn [row]
              ((apply juxt
                      (map (fn [column] (fn [m] (m column))) columns))
               row)))
           (map md-row))
          (tc/rows data :as-maps)))))

(md-table
 {:columns ["Foo" "Bar" "Baz"]
  :alignment-spec [":---" ":---:" "---:"]
  :data (tc/dataset {"Foo" ["foo0" "foo1" "foo2"]
                     "Bar" ["bar0" "bar1" "bar2"]
                     "Baz" [12.0 1.00 1.01]})})


;; You can also use `p/dataset->str` with `{:print-line-policy:markdown}`
;;
;; You need to strip out the table name as well if you don’t want that.
;;
;; You can do that with this lovely multiline java regular expression:
;;
;; `#"^(?s).+\n\n(.+)"`
;;
;; The `(?s)` at the front makes it multiline.

(clerk/md
 (as-> (tc/dataset {"Foo" ["foo0" "foo1" "foo2"]
                    "Bar" ["bar0" "bar1" "bar2"]
                    "Baz" [12.0 1.00 1.01]}) $
   (p/dataset->str $ {:print-line-policy :markdown})
   (re-find #"^(?s).+\n\n(.+)" $) ;; strip out the dataset name
   (last $)))
