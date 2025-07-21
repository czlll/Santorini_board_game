package edu.cmu.cs214.santorini.godcards;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Registry for God Cards that allows dynamic loading and retrieval.
 * This enables the extensible framework where new cards can be added
 * without modifying core game code.
 */
public class GodCardRegistry {
    private static final Map<String, Class<? extends GodCard>> registeredCards = new HashMap<>();
    
    static {
        // Register the built-in God Cards
        registerCard("Demeter", DemeterCard.class);
        registerCard("Hephaestus", HephaestusCard.class);
        registerCard("Minotaur", MinotaurCard.class);
        registerCard("Pan", PanCard.class);
    }
    
    /**
     * Registers a God Card class with the given name.
     * 
     * @param name the name of the card
     * @param cardClass the class implementing the God Card
     */
    public static void registerCard(String name, Class<? extends GodCard> cardClass) {
        registeredCards.put(name, cardClass);
    }
    
    /**
     * Creates a new instance of the specified God Card.
     * 
     * @param name the name of the card to create
     * @return a new instance of the God Card, or null if not found
     */
    public static GodCard createCard(String name) {
        Class<? extends GodCard> cardClass = registeredCards.get(name);
        if (cardClass == null) {
            return null;
        }
        
        try {
            return cardClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create God Card: " + name, e);
        }
    }
    
    /**
     * Gets the set of all registered God Card names.
     * 
     * @return the set of card names
     */
    public static Set<String> getAvailableCards() {
        return registeredCards.keySet();
    }
    
    /**
     * Checks if a God Card with the given name is registered.
     * 
     * @param name the name to check
     * @return true if the card is registered, false otherwise
     */
    public static boolean isCardRegistered(String name) {
        return registeredCards.containsKey(name);
    }
}