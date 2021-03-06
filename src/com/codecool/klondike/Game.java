package com.codecool.klondike;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import static com.codecool.klondike.CardSuit.DIAMONDS;

public class Game extends Pane {

    private List<Card> deck = new ArrayList<>();

    private Pile stockPile;
    private Pile discardPile;
    private List<Pile> foundationPiles = FXCollections.observableArrayList();
    private List<Pile> tableauPiles = FXCollections.observableArrayList();

    private double dragStartX, dragStartY;
    private List<Card> draggedCards = FXCollections.observableArrayList();

    private static double STOCK_GAP = 1;
    private static double FOUNDATION_GAP = 0;
    private static double TABLEAU_GAP = 30;


    private EventHandler<MouseEvent> onMouseClickedHandler = e -> {
        Card card = (Card) e.getSource();
        if (card.getContainingPile().getPileType() == Pile.PileType.STOCK) {
            card.moveToPile(discardPile);
            card.flip();
            card.setMouseTransparent(false);
            System.out.println("Placed " + card + " to the waste.");
        }
        if (card.getContainingPile().getName() == "Stock" && card.getContainingPile().isEmpty()){
            refillStockFromDiscard();
        }
        else if (card == card.getContainingPile().getTopCard() && card.isFaceDown() && card.getContainingPile().getPileType().equals(Pile.PileType.TABLEAU)){
            card.flip();
        }
    };

    private EventHandler<MouseEvent> stockReverseCardsHandler = e -> {
        if (stockPile.isEmpty()){
            refillStockFromDiscard();
        }
    };

    private EventHandler<MouseEvent> onMousePressedHandler = e -> {
        dragStartX = e.getSceneX();
        dragStartY = e.getSceneY();
    };

    private EventHandler<MouseEvent> onMouseDraggedHandler = e -> {
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        if (activePile.getPileType() == Pile.PileType.STOCK)
            return;
        double offsetX = e.getSceneX() - dragStartX;
        double offsetY = e.getSceneY() - dragStartY;

        draggedCards.clear();
        if (card.isFaceDown()){
            return;
        }else{
            ObservableList<Card> cards=activePile.getCards();
            int n=cards.size();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i)==card){
                    n=i;
                }
                if (n<=i){
                    draggedCards.add(cards.get(i));
                    cards.get(i).toFront();
                    cards.get(i).getDropShadow().setRadius(20);
                    cards.get(i).getDropShadow().setOffsetX(10);
                    cards.get(i).getDropShadow().setOffsetY(10);
                    cards.get(i).setTranslateX(offsetX);
                    cards.get(i).setTranslateY(offsetY);

                }
            }
        }
    };

    private EventHandler<MouseEvent> onMouseReleasedHandler = e -> {
        if (draggedCards.isEmpty())
            return;
        Card card = (Card) e.getSource();
        Pile activePile = card.getContainingPile();
        Pile pile = getValidIntersectingPile(card, tableauPiles);
        pile = pile != null ? pile: getValidIntersectingPile(card, foundationPiles);

        if (pile != null) {
            int cards=draggedCards.size();
            handleValidMove(card, pile);
            if (activePile.getCards().size() - cards > 0 && activePile.getPileType() == Pile.PileType.TABLEAU){
                Card cardToFlip = activePile.getCards().get(activePile.getCards().size() - cards - 1);
                if (cardToFlip.isFaceDown()) cardToFlip.flip();
            }
            isGameWon();
        }
        else{
            draggedCards.forEach(MouseUtil::slideBack);
            draggedCards = FXCollections.observableArrayList();
        }
        isGameWon();
    };

    private void winGameScreen() {
        Alert winBox = new Alert(Alert.AlertType.INFORMATION);
        winBox.setTitle("Congrats Biatch!");
        winBox.setHeaderText(null);
        winBox.setContentText("Yo!");
        winBox.showAndWait();
    }

    public boolean isGameWon() {
        for (Pile piles :foundationPiles){
            if (piles.numOfCards() != 13){
                return false;
            }
        }
        winGameScreen();
        return true;
    }

    public Game() {
        deck = Card.shuffleDeck();
        initPiles();
        dealCards();
    }

    public void addMouseEventHandlers(Card card) {
        card.setOnMousePressed(onMousePressedHandler);
        card.setOnMouseDragged(onMouseDraggedHandler);
        card.setOnMouseReleased(onMouseReleasedHandler);
        card.setOnMouseClicked(onMouseClickedHandler);
    }

    public void refillStockFromDiscard() {
        ObservableList<Card> cards = discardPile.getCards();
        for (int i = discardPile.getCards().size()-1; i >= 0; i--) {
            cards.get(i).flip();
            stockPile.addCard(cards.get(i));
        }
        discardPile.clear();
        System.out.println("Stock refilled from discard pile.");
    }

    public boolean isMoveValid(Card card, Pile destPile) {
        if (destPile.getPileType().equals(Pile.PileType.TABLEAU)){
            if (destPile.isEmpty() ){
                if (card.getRank().equals(CardRank.KING)){
                    return true;
                }else{
                    return false;
                }
            }
            if (card.getRank().getCardRank() == destPile.getTopCard().getRank().getCardRank() - 1 && Card.isOppositeColor(card, destPile.getTopCard())){
                return true;
            }else{
                return false;
            }
        }
        else if (destPile.getPileType().equals(Pile.PileType.FOUNDATION)){
            if (destPile.isEmpty()){
                if (card.getRank().equals(CardRank.ACE)) return true;
                else return false;
            }
            else{
                if (card.getRank().getCardRank() == destPile.getTopCard().getRank().getCardRank() + 1 &&
                        card.getSuit().equals(destPile.getTopCard().getSuit())){
                    return true;
                }
                else return false;
            }
        }
        else{
            return false;
        }

    }
    private Pile getValidIntersectingPile(Card card, List<Pile> piles) {
        Pile result = null;
        for (Pile pile : piles) {
            if (!pile.equals(card.getContainingPile()) &&
                    isOverPile(card, pile) &&
                    isMoveValid(card, pile))
                result = pile;
        }
        return result;
    }

    private boolean isOverPile(Card card, Pile pile) {
        if (pile.isEmpty())
            return card.getBoundsInParent().intersects(pile.getBoundsInParent());
        else
            return card.getBoundsInParent().intersects(pile.getTopCard().getBoundsInParent());
    }

    private void handleValidMove(Card card, Pile destPile) {
        String msg = null;
        if (destPile.isEmpty()) {
            if (destPile.getPileType().equals(Pile.PileType.FOUNDATION))
                msg = String.format("Placed %s to the foundation.", card);
            if (destPile.getPileType().equals(Pile.PileType.TABLEAU))
                msg = String.format("Placed %s to a new pile.", card);
        } else {
            msg = String.format("Placed %s to %s.", card, destPile.getTopCard());
        }
        System.out.println(msg);
        MouseUtil.slideToDest(draggedCards, destPile);
        draggedCards.clear();
    }


    private void initPiles() {
        getChildren().clear();
        foundationPiles.clear();
        tableauPiles.clear();

        stockPile = new Pile(Pile.PileType.STOCK, "Stock", STOCK_GAP);
        stockPile.setBlurredBackground();
        stockPile.setLayoutX(95);
        stockPile.setLayoutY(20);
        stockPile.setOnMouseClicked(stockReverseCardsHandler);
        getChildren().add(stockPile);

        discardPile = new Pile(Pile.PileType.DISCARD, "Discard", STOCK_GAP);
        discardPile.setBlurredBackground();
        discardPile.setLayoutX(285);
        discardPile.setLayoutY(20);
        getChildren().add(discardPile);


        for (int i = 0; i < 4; i++) {
            Pile foundationPile = new Pile(Pile.PileType.FOUNDATION, "Foundation " + i, FOUNDATION_GAP);
            foundationPile.setBlurredBackground();
            foundationPile.setLayoutX(610 + i * 180);
            foundationPile.setLayoutY(20);
            foundationPiles.add(foundationPile);
            getChildren().add(foundationPile);
        }
        for (int i = 0; i < 7; i++) {
            Pile tableauPile = new Pile(Pile.PileType.TABLEAU, "Tableau " + i, TABLEAU_GAP);
            tableauPile.setBlurredBackground();
            tableauPile.setLayoutX(95 + i * 180);
            tableauPile.setLayoutY(275);
            tableauPiles.add(tableauPile);
            getChildren().add(tableauPile);
        }
    }

    public void dealCards() {
        Iterator<Card> deckIterator = deck.iterator();
        Iterator<Pile> tableauIterator = tableauPiles.iterator();
        int tableauSize = 1;
        while (tableauIterator.hasNext()) {
            Pile tableau = tableauIterator.next();
            for (int i = 0; i < tableauSize; i++) {
                Card card = deckIterator.next();
                tableau.addCard(card);
                addMouseEventHandlers(card);
                if (i == tableauSize - 1) {
                    card.flip();
                }
                getChildren().add(card);
            }
            tableauSize++;
        }

        deckIterator.forEachRemaining(card -> {
            stockPile.addCard(card);
            addMouseEventHandlers(card);
            getChildren().add(card);
        });

    }

    public void setTableBackground(Image tableBackground) {
        setBackground(new Background(new BackgroundImage(tableBackground,
                BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT,
                BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));
    }

    public void setButtons() {
        Button restartButton = new Button("Restart");
        restartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deck = Card.shuffleDeck();
                initPiles();
                dealCards();
                setButtons();
            }
        });
        getChildren().add(restartButton);
        restartButton.setLayoutX(20);
        restartButton.setLayoutY(20);
    }

}
