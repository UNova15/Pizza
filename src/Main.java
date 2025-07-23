import java.util.LinkedList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

interface Get {
    String getName();
    double getPrice();
}

interface Set {
    void setName(String name);
    void setPrice(double price);
}

abstract class AbstractComponent implements Get, Set {
    private String name;
    private double price;

    AbstractComponent(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

class Ingredient extends AbstractComponent {

    Ingredient(String name, double price) {
        super(name, price);
    }

    Ingredient(String[] stringValues) {
        super(stringValues[0], Double.parseDouble(stringValues[1]));
    }

}

class PizzaBase extends AbstractComponent {

    PizzaBase(String name, double price) {
        super(name, price);
    }

    PizzaBase(String[] stringValues) {
        super(stringValues[0], Double.parseDouble(stringValues[1]));
    }
}

class Pizza implements Get {
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
        price = pizzaBase.getPrice();

        for (Ingredient i : ingredients) {
            price += i.getPrice();
        }
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    void setName(String newName) {
        name = newName;
    }

    void setIngredients(LinkedList<Ingredient> newIngredients) {
        ingredients = newIngredients;
        priceUpdate();
    }

    void setPizzaBase(PizzaBase newPizzaBase) {
        pizzaBase = newPizzaBase;
        priceUpdate();
    }
}


class Menu {
    private LinkedList<Ingredient> pizzaIngredients;
    private LinkedList<PizzaBase> pizzaBases;
    private LinkedList<Pizza> pizza;

    Menu(String ingredientsFile, String basesFile) {
        pizza = new LinkedList<>();
        pizzaBases = new LinkedList<>();
        pizzaIngredients = new LinkedList<>();

        readIngredientsFromFile(ingredientsFile, basesFile);
    }


    private boolean checkBases(PizzaBase classic) {
        double price = classic.getPrice();
        for (PizzaBase i : pizzaBases) {
            if ((100 * i.getPrice() / price) > 120.0) {
                return false;
            }
        }
        return true;
    }

    void readIngredientsFromFile(String ingredientsFile, String basesFile) {
        try {

            Scanner scanner = new Scanner(new File(ingredientsFile));

            while (scanner.hasNextLine()) {
                pizzaIngredients.add(new Ingredient(scanner.nextLine().split(" ")));
            }
            scanner.close();

            PizzaBase classic = null;
            scanner = new Scanner(new File(basesFile));
            while (scanner.hasNextLine()) {
                PizzaBase newBase = new PizzaBase(scanner.nextLine().split(" "));

                if (newBase.getName().equals("Классическое")) {
                    classic = newBase;
                }
                pizzaBases.add(newBase);
            }

            if (classic == null || !checkBases(classic)) {
                throw new IllegalArgumentException();
            }
            scanner.close();


        } catch (FileNotFoundException exc) {
            System.err.println("Ошибка чтения файла");
        } catch (IllegalArgumentException exc) {
            pizzaBases = null;
            System.err.println("Ошибка содержимого файла");
        }
    }


    private <T extends Get> LinkedList<T> searchInMenu(LinkedList<T> listIngredients, String... ingredientsName) {
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
            if (!flag) {
                throw new IllegalArgumentException();
            }
        }

        return ingredients;
    }

    LinkedList<Ingredient> getIngredients() {
        return pizzaIngredients;
    }

    LinkedList<Ingredient> getIngredients(String... ingredientsName) {
        return searchInMenu(pizzaIngredients, ingredientsName);
    }


    LinkedList<PizzaBase> getBases() {
        return pizzaBases;
    }

    PizzaBase getBases(String basesName) {
        return searchInMenu(pizzaBases, basesName).getFirst();
    }

    LinkedList<Pizza> getPizza() {
        return pizza;
    }

    Pizza getPizza(String pizzaName) {
        return searchInMenu(pizza, pizzaName).getFirst();
    }

    <T extends AbstractComponent> void change(String name, String newName, double newPrice, LinkedList<T> pizzaIngredients) {
        T ingredient = searchInMenu(pizzaIngredients, name).getFirst();
        ingredient.setName(newName);
        ingredient.setPrice(newPrice);
    }

    void change(String name, String newName, LinkedList<Ingredient> newIngredients, PizzaBase newBase) {
        Pizza changePizza = searchInMenu(pizza, name).getFirst();
        changePizza.setName(newName);
        changePizza.setPizzaBase(newBase);
        changePizza.setIngredients(newIngredients);
    }

    void addIngredient(String name, double price) {
        pizzaIngredients.add(new Ingredient(name, price));
    }

    void addBase(String name, double price) {
        pizzaBases.add(new PizzaBase(name, price));
    }

    void addPizza(String name, PizzaBase base, LinkedList<Ingredient> ingredients) {
        pizza.add(new Pizza(name, base, ingredients));
    }

    void addPizza(Pizza pizza){
        this.pizza.add(pizza);
    }

    <T extends Get> void delete(String name, LinkedList<T> components) {
        components.remove(searchInMenu(components, name).getFirst());
    }

    <T extends Get> void print(LinkedList<T> componentList) {
        for (T i : componentList) {
            System.out.println(i.getName() + " " + i.getPrice());
        }
    }

}

class PizzaConstructor {
    private String ingredientsFile;
    private String basesFile;

    Menu menu;

    PizzaConstructor(String ingredientsFile, String basesFile) {
        this.basesFile = basesFile;
        this.ingredientsFile = ingredientsFile;
        this.menu = new Menu(ingredientsFile, basesFile);
    }

    Pizza createPizza(String pizzaName, String pizzaBaseName, String... ingredientsName) {
        return new Pizza(pizzaName, menu.getBases(pizzaBaseName), menu.getIngredients(ingredientsName));
    }
}


class Main {
    public static void main(String[] args) {
        PizzaConstructor constructor = new PizzaConstructor("C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Ingredients.txt", "C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Bases.txt");

    }
}