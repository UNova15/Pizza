import java.util.LinkedList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

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

    Ingredient(String[] stringValues) {
        super(stringValues[0], Double.parseDouble(stringValues[1]));
    }

}

class PizzaBase extends AbstractIngredient {

    PizzaBase(String name, double price) {
        super(name, price);
    }

    PizzaBase(String[] stringValues) {
        super(stringValues[0], Double.parseDouble(stringValues[1]));
    }
}

class Pizza {

    private String name;
    private double price;

    private LinkedList<Ingredient> ingredients;
    private PizzaBase pizzaBase;

    Pizza(String name, PizzaBase pizzabase, LinkedList<Ingredient> ingredients) {
        this.name = name;
        this.pizzaBase = pizzabase;

        this.ingredients = ingredients;
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


class Menu {
    private LinkedList<Ingredient> pizzaIngredients = new LinkedList<>();
    private LinkedList<PizzaBase> pizzaBases = new LinkedList<>();

    Menu(String ingredientsFile, String basesFile) {
        readIngredientsFromFile(ingredientsFile, basesFile);
    }

    void readIngredientsFromFile(String ingredientsFile, String basesFile) {
        try {

            Scanner scanner = new Scanner(new File(ingredientsFile));

            while (scanner.hasNextLine()) {
                pizzaIngredients.add(new Ingredient(scanner.nextLine().split(" ")));
            }
            scanner.close();


            scanner = new Scanner(new File(basesFile));
            while (scanner.hasNextLine()) {
                pizzaBases.add(new PizzaBase(scanner.nextLine().split(" ")));
            }
            scanner.close();

        } catch (FileNotFoundException exc) {
            System.err.println("Ошибка чтения файла");
        }
    }

    private <T extends AbstractIngredient> LinkedList<T> searchInMenu(LinkedList<T>listIngredients,String... ingredientsName){
        LinkedList<T> ingredients = new LinkedList<>();

        for (String i : ingredientsName) {
            boolean flag = false;
            for (T j : listIngredients) {
                if (i.equals(j.getName())) {
                    ingredients.add(j);
                    flag = true;
                    break;
                }
            }
            if (!flag){
                throw new IllegalArgumentException();
            }
        }

        return ingredients;
    }

    LinkedList<Ingredient> getIngredients() {
        return pizzaIngredients;
    }

    LinkedList<Ingredient> getIngredients(String... ingredientsName) {
        return searchInMenu(pizzaIngredients,ingredientsName);
    }


    LinkedList<PizzaBase> getBases() {
        return pizzaBases;
    }

    PizzaBase getBases(String basesName){
        return searchInMenu(pizzaBases,basesName).getFirst();
    }

    void changeIngredient(String name,String newName,double newPrice){
        Ingredient ingredient = searchInMenu(pizzaIngredients,name).getFirst();
        ingredient.setName(newName);
        ingredient.setPrice(newPrice);
    }

    void changeBase(String name, String newName,double newPrice){
        PizzaBase base = searchInMenu(pizzaBases,name).getFirst();
        base.setName(newName);
        base.setPrice(newPrice);
    }

}

class PizzaConstructor {
    private String ingredientsFile;
    private String basesFile;

    private Menu menu;

    PizzaConstructor(String ingredientsFile, String basesFile) {
        this.basesFile = basesFile;
        this.ingredientsFile = ingredientsFile;
        this.menu = new Menu(ingredientsFile, basesFile);
    }

    Pizza createPizza(String pizzaName,String pizzaBaseName, String... ingredientsName) {
        return new Pizza(pizzaName,menu.getBases(pizzaBaseName),menu.getIngredients(ingredientsName));
    }

}


class Main {
    public static void main(String[] args) {
        PizzaConstructor constructor = new PizzaConstructor("C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Ingredients.txt","C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Bases.txt");
    }
}