(ns game.view
  (:require [clojure.string :as str]))

(defn format-value [value-width value]
  (format (str "%" (inc value-width) "d |") value))

(defn get-empty-line [{:keys [size value-width]}]
  (str "|"
       (apply str (repeat size (apply str (concat (repeat (+ 2 value-width) \space) [\|]))))))

(defn get-top-line [{:keys [size value-width]}]
  (str " "
       (apply str (repeat (dec (* size (+ 3 value-width))) \-))))

(defn format-row [row {:keys [value-width] :as game}]
  (let [top-line (get-top-line game)
        empty-line (get-empty-line game)
        values (str "|"
                    (apply str (map (partial format-value value-width) row)))]
    (str top-line "\n" empty-line "\n" values)))

(defn format-board [{:keys [board] :as game}]
  (as-> board $
    (map #(format-row % game) $)
    (concat $ [(get-top-line game)])
    (str/join "\n" $)))
