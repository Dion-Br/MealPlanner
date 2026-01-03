# Smart Meal Planner & Grocery List Generator

**Authors**: Dion Brovina, Amal Laassikri  
**Class**: 5 Software Design  
**Year**: 2025-2026  

---
## Design patterns
### 1. **MVC (Model-View-Controller)**
- **Model**: [Recipe](./project/src/main/java/be/uantwerpen/sd/project/model/domain/Recipe.java), [Ingredient](./project/src/main/java/be/uantwerpen/sd/project/model/domain/Ingredient.java), 
[WeeklyMealPlan](./project/src/main/java/be/uantwerpen/sd/project/model/domain/WeeklyMealPlan.java), [DayPlan](./project/src/main/java/be/uantwerpen/sd/project/model/domain/DayPlan.java),  [PlannedMeal](./project/src/main/java/be/uantwerpen/sd/project/model/domain/PlannedMeal.java), [GroceryItem](./project/src/main/java/be/uantwerpen/sd/project/model/domain/GroceryItem.java)
- **View**: [RecipeFxView](./project/src/main/java/be/uantwerpen/sd/project/view/RecipeFxView.java) (implements `RecipeView`), [WeeklyMealPlanFxView](./project/src/main/java/be/uantwerpen/sd/project/view/WeeklyMealPlanFxView.java) (implements `WeeklyMealPlanView`), [GroceryFxView](./project/src/main/java/be/uantwerpen/sd/project/view/GroceryFxView.java)
- **Controller**: [RecipeController](./project/src/main/java/be/uantwerpen/sd/project/controller/RecipeController.java), [WeeklyMealPlanController](./project/src/main/java/be/uantwerpen/sd/project/controller/WeeklyMealPlanController.java)

### 2. **Singleton (Thread-Safe)**
- **Class**: [RecipeRepository](./project/src/main/java/be/uantwerpen/sd/project/repository/RecipeRepository.java)
- **Implementation**: Double-checked locking pattern


### 3. **Observer**
- **Classes**: `WeeklyMealPlan`, [GroceryListGenerator](./project/src/main/java/be/uantwerpen/sd/project/model/domain/GroceryListGenerator.java), `PlannedMeal`, `DayPlan`, `RecipeRepository`
- **Implementation**: Uses Java's `PropertyChangeSupport` and `PropertyChangeListener`
- **Purpose**:
  - `WeeklyMealPlan` notifies observers when meals are added/removed
  - `GroceryListGenerator` observes meal plan changes to regenerate grocery list
  - `RecipeRepository` notifies listeners when recipes change
  - Automatic cascade: `PlannedMeal` → `DayPlan` → `WeeklyMealPlan` → `GroceryListGenerator`


### 4. **Builder**
- **Class**: [RecipeBuilder](./project/src/main/java/be/uantwerpen/sd/project/model/RecipeBuilder.java)
- **Purpose**: Constructs `Recipe` objects with flexible component addition
- **Usage**: In [RecipeService](./project/src/main/java/be/uantwerpen/sd/project/service/RecipeService.java)`.buildRecipe()`

### 5. **Composite**
- **Abstract Component**: `MealComponent`
- **Leaf**: `Ingredient`
- **Composite**: `Recipe`
- **Purpose**: Recipes can contain ingredients or other recipes (sub-recipes), creating a tree structure
- **Key Method**: `getIngredients()` recursively collects all ingredients from the entire tree
---
## Core Features
### Recipe Management
- **Add/Edit/Remove** recipes with name, description, and components
- **Components** can be:
  - Ingredients (with quantity, unit, and tags)
  - Sub-recipes (nested recipes)
- **Tags**: Automatically calculated from common ingredient tags (e.g., vegetarian, quick)
- **View**: Displays full recipe details including flattened ingredient list

### Weekly Meal Plan
- **Meal Types**: Breakfast, Lunch, Dinner, Snacks
- **Functionality**:
  - Add recipes to specific day/meal slots
  - Filter recipes by tags
  - Remove planned meals
  - View entire week at a glance

### Grocery List
- **Auto-generated** from weekly meal plan
- **Smart aggregation**: Sums quantities of same ingredient
- **Unit conversion**: Converts to base units before summing
- **Interactive**:
  - Check off items as bought
  - Add manual items
  - Preserves "bought" status during regeneration
---
## Tests

### Unit Tests
- **Test Class**: [RecipeServiceUTest](./project/src/test/java/be/uantwerpen/sd/project/RecipeServiceUTest.java)
- **Target Class**: `RecipeService`

### Integration Tests
- **Test Class**: [RecipeControllerITest](./project/src/test/java/be/uantwerpen/sd/project/RecipeControllerITest.java)
- **Target Class**: `RecipeController`

### Running All Tests
To run all tests, run the following command:
```bash
mvn test
```