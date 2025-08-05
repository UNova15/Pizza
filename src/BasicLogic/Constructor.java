package BasicLogic;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.function.Function;

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

    @Override
    public boolean equals(Object newObject) {
        if (this == newObject) {
            return true;
        }
        if (newObject instanceof Get) {
            return this.name.equals(((Get) newObject).getName());
        }
        return false;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
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

        if (stringValues.length < 3) throw new IllegalArgumentException();

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

    PizzaSides getSides() {
        return sides;
    }

    void setIngredients(LinkedList<Ingredient> newIngredients) {
        ingredients = newIngredients;
        updatePrice();
    }

    void setSides(PizzaSides newSides) {
        sides = newSides;
        updatePrice();
    }

    void doublingIngredients() {
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

        checkSides(pieces);
        priceUpdate();
    }

    private void checkSides(PieceOfPizza[] pieces) {
        for (PieceOfPizza piece : pieces) {
            if (!piece.getSides().getListOfPizzasUsed().contains(name)) {
                throw new IllegalArgumentException();
            }
        }
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

    PieceOfPizza[] getPieces() {
        return pieces;
    }

    PizzaBase getBase() {
        return pizzaBase;
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
        if (numberOfPieces <= 0) throw new IllegalArgumentException();

        int oldSize = pieces.length;
        pieces = Arrays.copyOf(pieces, numberOfPieces);

        for (int i = oldSize; i < numberOfPieces; i++) {
            pieces[i] = pieces[oldSize - 1];
        }
    }

    //TO DO заменить на копирование с помощью метода
    void setPieces(int startPosition, int endPosition, PieceOfPizza[] pieces) {
        for (int i = startPosition; i < endPosition; i++) {
            this.pieces[i] = pieces[i - startPosition];
        }
    }
}

class FileLoader {
    static void loadMenuData(String ingredientsFile,String baseFile,String sidesFile,LinkedList<Ingredient> pizzaIngredients,
     LinkedList<PizzaBase> pizzaBases,
     LinkedList<PizzaSides> pizzaSides){
        readItem(ingredientsFile,pizzaIngredients,Ingredient::new);
        readItem(baseFile,pizzaBases,PizzaBase::new);
        readItem(sidesFile,pizzaSides,PizzaSides::new);

        checkBases(pizzaBases);
    }

    private static <T> void readItem(String fileName, LinkedList<T> list, Function<String[], T> creator) {
        try (Scanner scanner = new Scanner(new File(fileName))) {

            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(" ");
                list.add(creator.apply(data));
            }

        } catch (FileNotFoundException exc) {
            System.err.println("Ошибка открытия файла");
        }
    }

    private static void checkBases(LinkedList<PizzaBase> pizzaBases) {
        PizzaBase classic = pizzaBases.stream().filter(b -> "Классическое".equals(b.getName())).findFirst().orElseThrow(IllegalArgumentException::new);

        if(pizzaBases.stream().anyMatch(b->b.getPrice()>classic.getPrice()*1.2)){
            throw new IllegalArgumentException();
        }
    }
}


class Menu {
    private LinkedList<Ingredient> pizzaIngredients;
    private LinkedList<PizzaBase> pizzaBases;
    private LinkedList<Pizza> pizza;
    private LinkedList<PizzaSides> pizzaSides;

    Menu(String ingredientsFile, String basesFile,String sidesFile) {
        pizza = new LinkedList<>();
        pizzaBases = new LinkedList<>();
        pizzaIngredients = new LinkedList<>();
        pizzaSides = new LinkedList<>();

        FileLoader.loadMenuData(ingredientsFile,basesFile,sidesFile,pizzaIngredients,pizzaBases,pizzaSides);
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
        return pizzaSides;
    }

    PizzaSides getSides(String sidesName) {
        return searchInMenu(pizzaSides, sidesName).getFirst();
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
        this.pizzaSides.add(new PizzaSides(name, price, pizzaSides));
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


    //создание пиццы из списка пицц
    LinkedList<Pizza> createNewPizza(int numberOfPieces, boolean doubled, String... pizzaName) {
        LinkedList<Pizza> newPizza = menu.searchInMenu(menu.getPizza(), pizzaName);

        for (Pizza pizza : newPizza) {
            pizza.setNumberOfPieces(numberOfPieces);

            if (doubled) {
                for (PieceOfPizza pieces : pizza.getPieces()) {
                    pieces.doublingIngredients();
                }
            }
        }

        return newPizza;
    }

    //создание пиццы из n-пиц
    Pizza createNewPizza(int numberOfPieces, String... pizzaName) {
        LinkedList<Pizza> pizzas = menu.searchInMenu(menu.getPizza(), pizzaName);
        Pizza newPizza = pizzas.getFirst();

        for (Pizza pizza : pizzas) {
            if (!(newPizza.getBase().equals(pizza.getBase()))) {
                throw new IllegalArgumentException();
            }
        }
        newPizza.setNumberOfPieces(numberOfPieces);

        int partSize = newPizza.getPieces().length / pizzaName.length;

        for (int i = 0; i < newPizza.getPieces().length; i += partSize) {
            newPizza.setPieces(i, i + partSize, pizzas.get(i / partSize).getPieces());
        }
        return newPizza;
    }

    //создание собственной пиццы
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


class Constructor {
    public static void main(String[] args) {
        PizzaConstructor constructor = new PizzaConstructor("C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Ingredients.txt", "C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\Bases.txt");

    }
}