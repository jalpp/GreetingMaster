package org.example;


/**
 * This Enum object represents a type of Greeting a user can perform.
 *
 * Enum consists of following method that abstract away how a greeting is called
 *   - toString()
 *       for backend purposes
 *   - getProperName()
 *      for frontend (end user purposes)
 *   - enduserMsgConverter()
 *      for frontend (end user purposes)
 *
 */


public enum Greetings {


    // Enum Fields for each greeting type

    GOOD_MORNING,

    GOOD_AFTERNOON,

    GOOD_NIGHT;

    /**
     * This method is responsible for returning the value-key mapping for the target
     * Greeting Enum to corresponding key in mongo player document.
     * @return String containing the key
     */

    @Override
    public String toString() {
        switch (this){
            case GOOD_NIGHT -> {
                return "gn";
            }
            case GOOD_AFTERNOON -> {
                return "ga";
            }
            case GOOD_MORNING -> {
                return "gm";
            }
        }
        return null;
    }

    /**
     * This method is responsible for returning the value-key mapping for the target
     * Greeting Enum to corresponding key in end user discord message.
     * @return String containing the key for end user
     */


    public String enduserMsgConverter(){
        switch (this){
            case GOOD_AFTERNOON -> {
                return "Ga";
            }
            case GOOD_NIGHT -> {
                return "Gn";
            }
            case GOOD_MORNING -> {
                return "Gm";
            }
        }
        return null;
    }

    /**
     * This method is responsible for returning the value-key mapping for the target
     * Greeting Enum to corresponding proper name of the greeting in first character
     * being a capital letter.
     * @return String containing proper name
     */

    public String getProperName(){
        String good = "Good";
        switch (this){
            case GOOD_AFTERNOON -> {
                return good + " Afternoon";
            }
            case GOOD_NIGHT -> {
                return good + " Night";
            }
            case GOOD_MORNING -> {
                return good + " Morning";
            }
        }
        return null;
    }

}
