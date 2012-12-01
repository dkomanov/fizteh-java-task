package ru.fizteh.fivt.students.kashinYana.bind.binderTestClasses;

import ru.fizteh.fivt.bind.AsXmlElement;
import ru.fizteh.fivt.bind.BindingType;
import ru.fizteh.fivt.bind.MembersToBind;


/**
 * Kashinskaya Yana, 195
 */

public class InnerClassTest {

    @BindingType(value = MembersToBind.GETTERS_AND_SETTERS)
    public static class InnerClass {
        protected InnerClass() {
            name = "YAHOO";
        }

        public InnerClass(String name) {
            this.name = name;
        }

        private String name;

        @AsXmlElement
        public String getName() {
            return name;
        }


        public void setName(String newName) {
            name = newName;
        }
    }
}
