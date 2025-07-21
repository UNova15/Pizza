import java.util.LinkedList;
import java.util.Arrays;

abstract class AbstractIngredient {
    private String name;
    private double price;

    AbstractIngredient(String name, double price) {
        this.name = name;
        this.price = price;
    }

    double getPrice() {
        return price;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    void setPrice(double price) {
        this.price = price;
    }
}

class Ingredient extends AbstractIngredient {

    Ingredient(String name, double price) {
        super(name, price);
    }

}

class PizzaBase extends AbstractIngredient {

    PizzaBase(String name, double price) {
        super(name, price);
    }

}

class Pizza {

    private String name;
    private double price;

    private LinkedList<Ingredient> ingredients;
    private PizzaBase pizzaBase;

    Pizza(String name, PizzaBase pizzabase, Ingredient... ingredients) {
        this.name = name;
        this.pizzaBase = pizzabase;

        this.ingredients = new LinkedList<>(Arrays.asList(ingredients));
        priceUpdate();
    }

    private void priceUpdate() {
        this.price = pizzaBase.getPrice();

        for (Ingredient i : ingredients) {
            price += i.getPrice();
        }
    }

    double getPrice() {
        return price;
    }

    String getName() {
        return name;
    }

    void setName(String newName) {
        name = newName;
    }

    void setIngredients(Ingredient... newIngredients) {
        ingredients = new LinkedList<>(Arrays.asList(newIngredients));
        priceUpdate();
    }

    void setPizzaBase(PizzaBase newPizzaBase) {
        pizzaBase = newPizzaBase;
    }
}


class Main {
    public static void main(String[] args) {

    }
}