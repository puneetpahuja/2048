(ns game.view
  (:require [clojure.string :as str]))

(defn format-cell [value {:keys [value-width]}]
  (str "|"
       (format (str "%" value-width "d") value)
       "|"))

(defn format-row [row game]
  (->> row
       (map #(format-cell % game))
       str/join))

(defn format-board [{:keys [board] :as game}]
  (->> board
       (map #(format-row % game))
       (str/join "\n")))
