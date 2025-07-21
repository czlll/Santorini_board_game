package edu.cmu.cs214.santorini.godcards;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test class for GodCardRegistry.
 */
public class GodCardRegistryTest {

    @Test
    public void testGetAvailableCards() {
        assertTrue(GodCardRegistry.getAvailableCards().contains("Demeter"));
        assertTrue(GodCardRegistry.getAvailableCards().contains("Hephaestus"));
        assertTrue(GodCardRegistry.getAvailableCards().contains("Minotaur"));
        assertTrue(GodCardRegistry.getAvailableCards().contains("Pan"));
        assertEquals(4, GodCardRegistry.getAvailableCards().size());
    }

    @Test
    public void testCreateCard() {
        GodCard demeter = GodCardRegistry.createCard("Demeter");
        assertNotNull(demeter);
        assertTrue(demeter instanceof DemeterCard);
        assertEquals("Demeter", demeter.getName());

        GodCard hephaestus = GodCardRegistry.createCard("Hephaestus");
        assertNotNull(hephaestus);
        assertTrue(hephaestus instanceof HephaestusCard);
        assertEquals("Hephaestus", hephaestus.getName());

        GodCard minotaur = GodCardRegistry.createCard("Minotaur");
        assertNotNull(minotaur);
        assertTrue(minotaur instanceof MinotaurCard);
        assertEquals("Minotaur", minotaur.getName());

        GodCard pan = GodCardRegistry.createCard("Pan");
        assertNotNull(pan);
        assertTrue(pan instanceof PanCard);
        assertEquals("Pan", pan.getName());
    }

    @Test
    public void testCreateCardInvalid() {
        GodCard invalid = GodCardRegistry.createCard("NonExistentCard");
        assertNull(invalid);
    }

    @Test
    public void testIsCardRegistered() {
        assertTrue(GodCardRegistry.isCardRegistered("Demeter"));
        assertTrue(GodCardRegistry.isCardRegistered("Hephaestus"));
        assertTrue(GodCardRegistry.isCardRegistered("Minotaur"));
        assertTrue(GodCardRegistry.isCardRegistered("Pan"));
        assertFalse(GodCardRegistry.isCardRegistered("NonExistentCard"));
    }
}