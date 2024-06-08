package at.aau.serg.websocketdemoapp.helper;

import lombok.Data;
import lombok.Getter;

@Data
public class Card {
    CardType cardType;
    @Getter
    String color;
    @Getter
    Integer value;
    String imgPath;

    public Card() {
    }

    public Card(CardType cardType, Integer value){
        this.cardType = cardType;
        this.color = cardType.getColor();
        this.value = value;
        this.createImagePath();
    }

    public Card(Card card) {
        this.cardType = card.cardType;
        this.color = card.color;
        this.value = card.value;
        this.imgPath = card.imgPath;
        this.createImagePath();
    }

    @Override
    public String toString() {
        return "card_" + cardType.getName() + this.value;
    }

    public void createImagePath() {
        this.imgPath =  "card_" + cardType.getName() + this.value;
        if (this.cardType.getName().equals("gaia") && !this.color.isEmpty()) {
            this.imgPath += "_" + color;
        }
    }

    public String getName() {
        return "card_" + cardType.getName() + value;
    }
}
