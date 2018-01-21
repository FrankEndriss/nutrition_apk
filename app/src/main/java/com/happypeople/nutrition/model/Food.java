package com.happypeople.nutrition.model;

/**
 * Immutable data class representing foods and ingredients.
 * Ingredients are categorized as fat, carbohydrates, sugar and proteins.
 * Sugar is interpreted as part of carbohydrates.
 */
public class Food {

    private final String name;
    private final int kCalPer100g;
    private final double fatPer100g;
    private final double carboPer100g;
    private final double sugarPer100g;
    private final double proteinPer100g;
    private final double defaultAmount;

    public Food(final String name, final int kCalPer100g, final double fatPer100g, final double carboPer100g, final double sugarPer100g, final double proteinPer100g, final double defaultAmount) {
        this.name = name;
        this.kCalPer100g = kCalPer100g;
        this.fatPer100g = fatPer100g;
        this.carboPer100g = carboPer100g;
        this.sugarPer100g = sugarPer100g;
        this.proteinPer100g = proteinPer100g;
        this.defaultAmount = defaultAmount;
    }

    /**
     * @return Name of this food, acts as ID.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The kCal per 100g value.
     */
    public int getKcalPer100g() {
        return kCalPer100g;
    }

    public double getFatPer100g() {
        return fatPer100g;
    }

    public double getCarboPer100g() {
        return carboPer100g;
    }

    public double getSugarPer100g() {
        return sugarPer100g;
    }

    public double getProteinPer100g() {
        return proteinPer100g;
    }

    public double getDefaultAmount() {
        return defaultAmount;
    }

    public String toString() {
        return getName();
    }
}
