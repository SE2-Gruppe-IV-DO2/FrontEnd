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
        this.imgPath = cardType.getName() + this.value + ".png";
    }

    public Card(Card card) {
        this.cardType = card.cardType;
        this.color = card.color;
        this.value = card.value;
        this.imgPath = card.imgPath;
    }

    @Override
    public String toString() {
        return color + " " + value;
    }
}
