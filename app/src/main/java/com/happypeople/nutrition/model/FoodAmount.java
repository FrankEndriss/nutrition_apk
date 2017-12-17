package com.happypeople.nutrition.model;

/** Amount of some Food. May be in grams or kcalories.
 */
public class FoodAmount {
    private final FoodUnit unit;
    private final int amount;

    public FoodAmount(final int amount, final FoodUnit unit) {
        this.unit=unit;
        this.amount=amount;
    }

    public FoodUnit getUnit() {
        return unit;
    }

    public int getAmount() {
        return amount;
    }

    public int getKcal(final Food food) {
        return unit.calcKcal(amount, food.getKcalPer100g());
    }

    public int getGrams(final Food food) {
        return unit.calcGrams(amount, food.getKcalPer100g());
    }

    /** Foods are meassured in kCal or grams. */
    public enum FoodUnit {

        KCAL {
            @Override
			public int calcKcal(final int amount, final int kcalPer100g) {
                return amount;
            };
            @Override
			public int calcGrams(final int amount, final int kcalPer100g) {
                return amount/kcalPer100g;
            }
			@Override
			public String getLabel() {
				return "kCal";
			}
        },
        GRAM {
            @Override
			public int calcKcal(final int amount, final int kcalPer100g) {
                return (amount*kcalPer100g)/100;
            };
            @Override
			public int calcGrams(final int amount, final int kcalPer100g) {
                return amount;
            }
			@Override
			public String getLabel() {
				return "gramm";
			}
        };

        public abstract int calcKcal(int amount, int kcalPer100g);
        public abstract int calcGrams(int amount, int kcalPer100g);
        public abstract String getLabel();
    };

}
