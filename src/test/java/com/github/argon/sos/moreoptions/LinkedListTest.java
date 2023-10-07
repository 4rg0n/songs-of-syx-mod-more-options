package com.github.argon.sos.moreoptions;

import org.junit.jupiter.api.Test;
import snake2d.util.sets.LinkedList;

import java.util.ArrayList;
import java.util.List;

public class LinkedListTest {
    @Test
    void add() {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);

        LinkedList<Integer> linkedList = new LinkedList<>();

        for (Integer integer : list) {
            linkedList.add(integer);
        }

        snake2d.util.sets.ArrayList<Integer> integers = new snake2d.util.sets.ArrayList<>(linkedList);

        for (Integer integer : integers) {
            System.out.println(integer);
        }
    }
}
