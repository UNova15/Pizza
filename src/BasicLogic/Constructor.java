package BasicLogic;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

interface Get {
    String getName();

    double getPrice();
}

interface Set {
    void setName(String name);

    void setPrice(double price);
}

interface ComponentUpdater {
    <T> void update(T component);
}

class Print {
    static <T extends Get> void printComponent(LinkedList<T> componentList) {
        for (T i : componentList) {
            System.out.println(i.getName() + "\t" + i.getPrice());
        }
    }

    static <T extends Get> void printComponent(LinkedList<T> componentList, Predicate<T> predicate){
        componentList.stream().filter(predicate).forEach(System.out::println);
    }
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

    LinkedList<Ingredient> getIngredients() {
        return ingredients;
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

    void doubleIngredients() {
        LinkedList<Ingredient> copy = new LinkedList<>(ingredients);
        ingredients.addAll(copy);
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

        checkSides(this.pieces);
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

    void setPieces(int position, PieceOfPizza pieces) {
        this.pieces[position] = pieces;
    }
}

class FileLoader {
    static void loadMenuData(String ingredientsFile, String baseFile, String sidesFile, LinkedList<Ingredient> pizzaIngredients,
                             LinkedList<PizzaBase> pizzaBases,
                             LinkedList<PizzaSides> pizzaSides) {
        readItem(ingredientsFile, pizzaIngredients, Ingredient::new);
        readItem(baseFile, pizzaBases, PizzaBase::new);
        readItem(sidesFile, pizzaSides, PizzaSides::new);

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
        PizzaBase classic = pizzaBases.stream().filter(b -> "Классическое".equals(b.getName()))
                .findFirst().
                orElseThrow(IllegalArgumentException::new);

        if (pizzaBases.stream().anyMatch(b -> b.getPrice() > classic.getPrice() * 1.2)) {
            throw new IllegalArgumentException();
        }
    }
}


class Menu {
    private LinkedList<Ingredient> pizzaIngredients;
    private LinkedList<PizzaBase> pizzaBases;
    private LinkedList<Pizza> pizza;
    private LinkedList<PizzaSides> pizzaSides;

    Menu(String ingredientsFile, String basesFile, String sidesFile) {
        pizza = new LinkedList<>();
        pizzaBases = new LinkedList<>();
        pizzaIngredients = new LinkedList<>();
        pizzaSides = new LinkedList<>();

        FileLoader.loadMenuData(ingredientsFile, basesFile, sidesFile, pizzaIngredients, pizzaBases, pizzaSides);
    }


    <T extends Get> LinkedList<T> searchInMenu(LinkedList<T> listIngredients, String... ingredientsName) {
        LinkedList<T> newList = new LinkedList<>();
        Map<String, T> itemMap = listIngredients.stream().collect(Collectors.toMap(T::getName, Function.identity()));

        for (String name : ingredientsName) {
            newList.add(itemMap.get(name));
        }
        return newList;
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

    <T extends Get> void changeComponent(String name, LinkedList<T> listComponents, ComponentUpdater updater) {
        T component = searchInMenu(listComponents, name).getFirst();

        updater.update(component);
    }

    //добавление объектов с произвольной длинной аргументов конструктора
    <T> void addComponent(String name, Double price, LinkedList<T> listComponents, Supplier<T> creator) {
        listComponents.add(creator.get());
    }

    //добавление объектов с 2-мя аргументами конструктора
    <T> void addComponent(String name, Double price, LinkedList<T> listComponents, BiFunction<String, Double, T> creator) {
        listComponents.add(creator.apply(name, price));
    }

    <T extends Get> void deleteComponent(String name, LinkedList<T> components) {
        components.remove(searchInMenu(components, name).getFirst());
    }

}

class Order implements Get {
    private double price;
    private int numberOfGuests;
    private UUID orderID;
    private LinkedList<Pizza> pizza;
    private String comments;
    private LocalDate date;
    private LocalDate postponedDate;

    Order(LinkedList<Pizza> newPizza, String comments,int numberOfGuests,Optional<LocalDate> postponedDate) {
        this.pizza = newPizza;
        this.numberOfGuests = numberOfGuests;
        this.comments = comments;
        this.date = LocalDate.now();
        this.orderID = UUID.randomUUID();
        this.price = priceCalculation();

        if (postponedDate.isPresent()) {
            this.postponedDate = postponedDate.get();
        }
    }

    private double priceCalculation(){
        double price = 0;

        for (Pizza i : pizza) {
            price += i.getPrice();
        }
        return price;
    }

    double getPriceForEachGuests(){
        return price/numberOfGuests;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return orderID.toString();
    }

    LocalDate getDate(){
        return date;
    }

}

class PizzaBuilder {
    private String name;
    private PizzaBase base;
    private LinkedList<PieceOfPizza> pieces;
    private boolean doubleIngredients;
    private Menu menu;

    PizzaBuilder(String name, Menu menu) {
        this.name = name;
        this.menu = menu;
    }

    PizzaBuilder withBase(String base) {
        this.base = menu.getBases(base);
        return this;
    }

    PizzaBuilder addPieces(String sidesName, String... ingredients) {
        pieces.add(new PieceOfPizza(menu.getSides(sidesName),
                menu.getIngredients(ingredients)));

        return this;
    }

    PizzaBuilder withDoubleIngredients(boolean doubleIngredients) {
        this.doubleIngredients = doubleIngredients;
        return this;
    }

    PizzaBuilder addPieceFromTemplate(PieceOfPizza piece) {
        pieces.add(new PieceOfPizza(piece.getSides(),
                new LinkedList<Ingredient>(piece.getIngredients())));

        return this;
    }

    Pizza build() {
        if (base == null || pieces.isEmpty()) throw new IllegalArgumentException();

        if (doubleIngredients) {
            for (PieceOfPizza piece : pieces) {
                piece.doubleIngredients();
            }
        }

        return new Pizza(name, base, pieces.toArray(new PieceOfPizza[0]));
    }

}

class PizzaConstructor {
    private final LinkedList<Order> savedOrder;
    private final Menu menu;

    PizzaConstructor(String ingredientsFile, String basesFile) {

        this.savedOrder = new LinkedList<>();
        this.menu = new Menu(ingredientsFile, basesFile, ingredientsFile);
    }

    Pizza createPizzaFromMenu(String pizzaName, int numberOfPizzaSlices, boolean doubleIngredients) {
        Pizza template = menu.getPizza(pizzaName);

        PizzaBuilder builder = new PizzaBuilder(pizzaName, menu)
                .withBase(template.getBase().getName())
                .withDoubleIngredients(doubleIngredients);

        for (int i = 0; i < numberOfPizzaSlices; i++) {
            builder.addPieceFromTemplate(template.getPieces()[0]);
        }
        return builder.build();
    }

    Pizza createCombinedPizza(String pizzaName, int numberOfPizzaSlices, String... pizzaNames) {
        PizzaBuilder builder = new PizzaBuilder(pizzaName, menu);
        PizzaBase commonBase = null;

        if (numberOfPizzaSlices % pizzaName.length() != 0) {
            throw new IllegalArgumentException();
        }

        for (String name : pizzaNames) {
            Pizza pizza = menu.getPizza(name);

            if (commonBase == null) {
                commonBase = pizza.getBase();
            } else if (commonBase.equals(pizza.getBase())) {
                throw new IllegalArgumentException();
            }

            for (int i = 0; i < numberOfPizzaSlices / pizzaNames.length; i++) {
                builder.addPieceFromTemplate(pizza.getPieces()[i]);
            }
        }
        return builder.build();
    }

    Pizza createCustomPizza(String pizzaName, String pizzaBase, String[] sides, String[][] ingredients) {
        PizzaBuilder builder = new PizzaBuilder(pizzaName, menu).withBase(pizzaBase);

        for (int i = 0; i < sides.length; i++) {
            builder.addPieces(sides[i], ingredients[i]);
        }
        return builder.build();

    }

    Order createOrder(LinkedList<Pizza> pizza, String comments,int numberOfGuests ,Optional<LocalDate> date) {
        Order newOrder = new Order(pizza, comments,numberOfGuests,date);
        savedOrder.add(newOrder);
        return newOrder;
    }

    LinkedList<Order> getSavedOrder(){
        return savedOrder;
    }
}


class Constructor {
    public static void main(String[] args) {
        PizzaConstructor constructor = new PizzaConstructor("C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\BasicLogic\\Ingredients.txt",
                "C:\\Users\\Kirill\\IdeaProjects\\Pizza\\src\\BasicLogic\\Bases.txt");

    }
}