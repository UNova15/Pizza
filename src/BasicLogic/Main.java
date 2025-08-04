package BasicLogic;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;

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

class PizzaSides extends AbstractComponent {
    private LinkedList<String> listOfPizzasUsed;

    PizzaSides(String name, double price, LinkedList<String> pizzaName) {
        super(name, price);
        listOfPizzasUsed = pizzaName;
    }

    PizzaSides(String[] stringValues) {
        super(stringValues[0], Double.parseDouble(stringValues[1]));
        listOfPizzasUsed = new LinkedList<>(Arrays.asList(stringValues).subList(2, stringValues.length));
    }

    LinkedList<String> getListOfPizzasUsed() {
        return listOfPizzasUsed;
    }

    void setListOfPizzasUsed(LinkedList<String> newPizzaSides) {
        listOfPizzasUsed = newPizzaSides;
    }
}

class PieceOfPizza {
    private double price;
    private PizzaSides sides;
    private LinkedList<Ingredient> ingredients;

    PieceOfPizza(PizzaSides sides, LinkedList<Ingredient> ingredients) {
        this.sides = sides;
        this.ingredients = ingredients;
        updatePrice();
    }

    private void updatePrice() {
        price = sides.getPrice();

        for (Ingredient i : ingredients) {
            price += i.getPrice();
        }
    }

    double getPrice() {
        return price;
    }

    void setIngredients(LinkedList<Ingredient> newIngredients) {
        ingredients = newIngredients;
        updatePrice();
    }

    void setSides(PizzaSides newSides) {
        sides = newSides;
        updatePrice();
    }

    void doublingIngredients(){
        ingredients.addAll(ingredients);
        updatePrice();
    }
}


class Pizza implements Get {
    private String name;
    private double price;

    private PizzaBase pizzaBase;
    private PieceOfPizza[] pieces;

    Pizza(String name, PizzaBase pizzaBase, PieceOfPizza[] pieces) {
        this.name = name;
        this.pizzaBase = pizzaBase;
        this.pieces = pieces;

        priceUpdate();
    }

    private void priceUpdate() {
        price = pizzaBase.getPrice();

        for (PieceOfPizza i : pieces) {
            price += i.getPrice();
        }
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    PieceOfPizza [] getPieces(){
        return pieces;
    }

    void setName(String newName) {
        name = newName;
    }

    void setIngredients(int position, LinkedList<Ingredient> newIngredients) {
        pieces[position].setIngredients(newIngredients);
        priceUpdate();
    }

    void setPizzaBase(PizzaBase newPizzaBase) {
        pizzaBase = newPizzaBase;
        priceUpdate();
    }

    void setSides(int position, PizzaSides newSides) {
        pieces[position].setSides(newSides);
        priceUpdate();
    }

    void setNumberOfPieces(int numberOfPieces) {
        int oldSize = pieces.length;
        pieces = Arrays.copyOf(pieces, numberOfPieces);

        for (int i = oldSize; i < numberOfPieces; i++) {
            pieces[i] = pieces[oldSize - 1];
        }
    }

    void setNumberOfPieces(PieceOfPizza[] newPieces) {
        pieces = newPieces;
    }
}


class Menu {
    private LinkedList<Ingredient> pizzaIngredients;
    private LinkedList<PizzaBase> pizzaBases;
    private LinkedList<Pizza> pizza;
    private LinkedList<PizzaSides> sides;

    Menu(String ingredientsFile, String basesFile) {
        pizza = new LinkedList<>();
        pizzaBases = new LinkedList<>();
        pizzaIngredients = new LinkedList<>();
        sides = new LinkedList<>();

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
                String[] newStrings = scanner.nextLine().split(" ");
                pizzaIngredients.add(new Ingredient(newStrings));
                sides.add(new PizzaSides(newStrings));

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


    <T extends Get> LinkedList<T> searchInMenu(LinkedList<T> listIngredients, String... ingredientsName) {
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

    LinkedList<PizzaSides> getSides() {
        return sides;
    }

    PizzaSides getSides(String sidesName) {
        return searchInMenu(sides, sidesName).getFirst();
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

    void change(String name, String newName, double newPrice, LinkedList<PizzaSides> pizzaIngredients, LinkedList<String> newListOfPizzasUsed) {
        PizzaSides pizzaSides = searchInMenu(pizzaIngredients, name).getFirst();
        pizzaSides.setName(newName);
        pizzaSides.setPrice(newPrice);
        pizzaSides.setListOfPizzasUsed(newListOfPizzasUsed);
    }

    void change(String name, int position, String newName, LinkedList<Ingredient> newIngredients, PizzaBase newBase) {
        Pizza changePizza = searchInMenu(pizza, name).getFirst();
        changePizza.setName(newName);
        changePizza.setPizzaBase(newBase);
        changePizza.setIngredients(position, newIngredients);
    }

    void addPizzaSides(String name, double price, LinkedList<String> pizzaSides) {
        sides.add(new PizzaSides(name, price, pizzaSides));
    }

    void addIngredient(String name, double price) {
        pizzaIngredients.add(new Ingredient(name, price));
    }

    void addBase(String name, double price) {
        pizzaBases.add(new PizzaBase(name, price));
    }

    void addPizza(Pizza pizza) {
        this.pizza.add(pizza);
    }

    <T extends Get> void delete(String name, LinkedList<T> components) {
        components.remove(searchInMenu(components, name).getFirst());
    }

    <T extends Get> void print(LinkedList<T> componentList) {
        for (T i : componentList) {
            System.out.println(i.getName() + "\t" + i.getPrice());
        }
    }

}

class Order implements Get {
    private double price;
    private UUID orderNumber;
    private LinkedList<Pizza> pizza;
    private String comments;
    private LocalDate date;
    private LocalDate postponedDate;

    Order(LinkedList<Pizza> newPizza, String comments, Optional<LocalDate> postponedDate) {
        this.pizza = newPizza;
        this.comments = comments;
        this.date = LocalDate.now();
        this.orderNumber = UUID.randomUUID();
        this.price = getPrice();

        if (postponedDate.isPresent()) {
            this.postponedDate = postponedDate.get();
        }
    }

    public double getPrice() {
        double price = 0;

        for (Pizza i : pizza) {
            price += i.getPrice();
        }
        return price;
    }

    public String getName() {
        return orderNumber.toString();
    }

}

class PizzaConstructor {
    private String ingredientsFile;
    private String basesFile;
    private LinkedList<Order> savedOrder;

    Menu menu;

    PizzaConstructor(String ingredientsFile, String basesFile) {
        this.basesFile = basesFile;
        this.ingredientsFile = ingredientsFile;

        this.savedOrder = new LinkedList<>();
        this.menu = new Menu(ingredientsFile, basesFile);
    }


    LinkedList<Pizza> createNewPizza(boolean doubled, int numberOfPieces, String... pizzaName) {
        LinkedList<Pizza> newPizza = menu.searchInMenu(menu.getPizza(), pizzaName);

        for (Pizza pizza : newPizza) {
            pizza.setNumberOfPieces(numberOfPieces);

            if(doubled){
                for(PieceOfPizza pieces:pizza.getPieces()){
                    pieces.doublingIngredients();
                }
            }
        }

        return newPizza;
    }

    Pizza createNewPizza(String namePizza, String pizzaBase, String[] pizzaSides, String[][] ingredients) {
        LinkedList<PieceOfPizza> newPieces = new LinkedList<>();

        PizzaBase base = menu.getBases(pizzaBase);
        LinkedList<PizzaSides> sides = menu.searchInMenu(menu.getSides(), pizzaSides);
        for (int i = 0; i < ingredients.length; i++) {
            LinkedList<Ingredient> newIngredients = menu.searchInMenu(menu.getIngredients(), ingredients[i]);
            newPieces.add(new PieceOfPizza(sides.poll(), newIngredients));
        }

        return new Pizza(namePizza, base, newPieces.toArray(new PieceOfPizza[pizzaSides.length]));
    }

    Order createOrder(LinkedList<Pizza> pizza, String comments, Optional<LocalDate> date) {
        Order newOrder = new Order(pizza, comments, date);
        savedOrder.add(newOrder);
        return newOrder;
    }
}


class Main {
    public static void main(String[] args) {
        PizzaConstructor constructor = new PizzaConstructor("C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Ingredients.txt", "C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Bases.txt");

    }
}