package io.github.romannbroque.fixture.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a class as a dataset provider for a specific entity.
 * This triggers the automatic generation of a fixture class during compilation.
 * The generated fixture allows easy creation and customization of the entity for testing purposes.
 *
 * <h2>Expected Structure of the Annotated Class</h2>
 * The annotated class must:
 * <ul>
 *     <li>Be a concrete class (not abstract).</li>
 *     <li>Declare the target entity via {@link #entityClass()}.</li>
 *     <li>Declare the data model class via {@link #dataModelClass()}.</li>
 *     <li>Provide a static method {@code build(DataModel model)} that takes the data model as input and returns the constructed entity.</li>
 * </ul>
 *
 * <h3>Flexible DataModel Location</h3>
 * The data model class can be:
 * <ul>
 *     <li>A nested static class inside the annotated class (recommended).</li>
 *     <li>Or any external class, as long as it is correctly referenced via {@link #dataModelClass()}.</li>
 * </ul>
 *
 * <h3>Optional Methods</h3>
 * The following static methods can optionally be provided in the annotated class:
 * <ul>
 *     <li>{@code buildValid()} - Returns a fully populated and valid instance of {@code DataModel}.
 *         If this method is absent, the generator will fallback to {@code new DataModel()} (default constructor).</li>
 *     <li>{@code buildInvalid()} - Returns a pre-defined invalid instance of {@code DataModel}, useful for negative test cases.
 *         This method is completely optional and only generated if present.</li>
 * </ul>
 *
 * <p>
 * Example:
 * <pre>
 * {@code
 * @GenerateFixture(
 *     entityClass = PurchaseContract.class,
 *     dataModelClass = PurchaseContractBaseDataSet.DataModel.class
 * )
 * public class PurchaseContractBaseDataSet {
 *
 *     public static class DataModel {
 *         public String contractReference = "20250001";
 *         public Double quantity = 1000.0;
 *     }
 *
 *     public static PurchaseContract build(DataModel model) {
 *         return new PurchaseContract(model.contractReference, model.quantity);
 *     }
 *
 *     public static DataModel buildValid() {
 *         return new DataModel();
 *     }
 *
 *     public static DataModel buildInvalid() {
 *         DataModel invalid = new DataModel();
 *         invalid.contractReference = null; // invalid case
 *         return invalid;
 *     }
 * }
 * }
 * </pre>
 *
 * <h3>Important Rules</h3>
 * <ul>
 *     <li>{@code build()} is mandatory.</li>
 *     <li>{@code buildValid()} is recommended but not required.</li>
 *     <li>{@code buildInvalid()} is entirely optional.</li>
 * </ul>
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface GenerateFixture {

  /**
   * Specifies the entity class this dataset is responsible for.
   * This is the class that will be built using the data model.
   *
   * @return The target entity class.
   */
  Class<?> entityClass();

  /**
   * Specifies the model class containing the raw data needed to build the entity.
   * This model acts as a simple data carrier (often a POJO) which the fixture manipulates.
   * It can be a nested class (inside the dataset provider) or any external class.
   *
   * @return The data model class.
   */
  Class<?> dataModelClass();
}
