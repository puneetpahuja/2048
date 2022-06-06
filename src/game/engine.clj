(ns game.engine
  (:gen-class)
  (:require [clojure.string :as str]
            [game.view :as view]))

(defn find-empty-locations [{:keys [state empty-element size]}]
  (for [row (range size)
        col (range size)
        :when (= empty-element (get-in state [row col]))]
    [row col]))

(defn add-element [{:keys [board new-elements] :as game}]
  (let [empty-locations (find-empty-locations game)
        location-to-fill (rand-nth empty-locations)
        new-element (rand-nth new-elements)]
    (assoc-in board location-to-fill new-element)))

(defn get-start-game [{:keys [size empty-value winning-value] :as game}]
  (let [empty-board (vec (repeat size (vec (repeat size empty-value))))
        value-width (count (str winning-value))
        updated-game (assoc game
                            :value-width value-width
                            :board empty-board)]
    (assoc updated-game
           :board (add-element updated-game))))

(defn -main
  []
  (->> (get-start-game {:size 4
                        :empty-value 0
                        :winning-value 2048
                        :new-elements [2 2 2 2 4]})
       view/format-board
       println))
