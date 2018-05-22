package cz.zcu.kiv.nlp.ir.trec.query;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class BooleanQueryTest {

    private Set<String> ahoj;
    private Set<String> oj;
    private Set<String> zder;

    @BeforeEach
    void setUp(){
        ahoj = new HashSet<>();
        oj = new HashSet<>();
        zder = new HashSet<>();

        ahoj.add("a");
        ahoj.add("h");
        ahoj.add("o");
        ahoj.add("j");

        oj.add("o");
        oj.add("j");

        zder.add( "z");
        zder.add( "d");
        zder.add( "e");
        zder.add( "r");
    }

    @Test
    void andMergeSetsTest() {
        List<Set<String>> list = new LinkedList<>();
        list.add(ahoj);
        list.add(oj);
        Set<String> restulr = BooleanQuery.andMergeSets(list);

        assert restulr != null;
        assertTrue(restulr.contains("o"));
        assertTrue(restulr.contains("j"));
        assertFalse(restulr.contains("a"));
        assertFalse(restulr.contains("h"));
    }

    @Test
    void orMergeSetsTest() {
        List<Set<String>> list = new LinkedList<>();
        list.add(ahoj);
        list.add(oj);
        Set<String> restulr = BooleanQuery.orMergeSets(list);

        assert restulr != null;
        assertTrue(restulr.contains("o"));
        assertTrue(restulr.contains("j"));
        assertTrue(restulr.contains("a"));
        assertTrue(restulr.contains("h"));
    }
}