package com.codecool.klondike;

import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import java.util.Random;


import java.util.*;

public class Card extends ImageView{

    private CardRank rank;
    private CardSuit suit;
    private boolean faceDown;

    private Image backFace;
    private Image frontFace;
    private Pile containingPile;
    private DropShadow dropShadow;

    static Image cardBackImage;
    private static final Map<String, Image> cardFaceImages = new HashMap<>();
    public static final int WIDTH = 150;
    public static final int HEIGHT = 215;

    public Card(CardSuit suit, CardRank rank, boolean faceDown) {
        this.suit = suit;
        this.rank = rank;
        this.faceDown = faceDown;
        this.dropShadow = new DropShadow(2, Color.gray(0, 0.75));
        backFace = cardBackImage;
        frontFace = cardFaceImages.get(getShortName());
        setImage(faceDown ? backFace : frontFace);
        setEffect(dropShadow);
    }

    public CardSuit getSuit() {
        return suit;
    }

    public CardRank getRank() {
        return rank;
    }


    public boolean isFaceDown() {
        return faceDown;
    }

    public String getShortName() {
        return "S" + suit + "R" + rank;
    }

    public DropShadow getDropShadow() {
        return dropShadow;
    }

    public Pile getContainingPile() {
        return containingPile;
    }

    public void setContainingPile(Pile containingPile) {
        this.containingPile = containingPile;
    }

    public void moveToPile(Pile destPile) {
        this.getContainingPile().getCards().remove(this);
        destPile.addCard(this);
    }

    public void flip() {
        faceDown = !faceDown;
        setImage(faceDown ? backFace : frontFace);
    }

    @Override
    public String toString() {
        return "The " + "Rank" + rank + " of " + "Suit" + suit;
    }

    public static boolean isOppositeColor(Card card1, Card card2) {
        //TODO
        return true;
    }

    public static boolean isSameSuit(Card card1, Card card2) {
        return card1.getSuit() == card2.getSuit();
    }

    public static List<Card> createNewDeck() {
        List<Card> result = new ArrayList<>();
        for (int suit = 0; suit < 4; suit++) {
            for (int rank = 0; rank < 13; rank++) {
                result.add(new Card(CardSuit.values()[suit], CardRank.values()[rank], true));
            }
        }
        return result;
    }
    public static List<Card> shuffleDeck(){
        List<Card> result=new ArrayList<>();
        result=createNewDeck();
        List<Card> shuffledDeck = new ArrayList<>();
        Random rand = new Random();
        int countCards=52;
        for (int i = 0; i < 52; i++) {
            int  n = rand.nextInt(countCards);
            shuffledDeck.add(result.get(n));
            result.remove(n);
            countCards--;
        }
        return shuffledDeck;
    }

    public static void loadCardImages() {
        cardBackImage = new Image("card_images/card_back.png");
        String suitName = "";
        for (int suit = 0; suit < 4; suit++) {
            switch (CardSuit.values()[suit]) {
                case HEARTS:
                    suitName = "hearts";
                    break;
                case DIAMONDS:
                    suitName = "diamonds";
                    break;
                case SPADES:
                    suitName = "spades";
                    break;
                case CLUBS:
                    suitName = "clubs";
                    break;
            }
            for (int rank = 1; rank < 14; rank++) {
                String cardName = suitName + rank;
                String cardId = "S" + CardSuit.values()[suit] + "R" + CardRank.values()[rank-1];
                String imageFileName = "card_images/" + cardName + ".png";
                cardFaceImages.put(cardId, new Image(imageFileName));
            }
        }
    }

}
