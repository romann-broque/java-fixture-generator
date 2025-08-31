/**
 * This package contains the core annotations and interfaces for the fixture generation mechanism.
 *
 * <p>Authors: Frédéric Foissey, Romann Broque</p>
 *
 * <p>
 * The {@link io.github.romannbroque.fixture.annotations.GenerateFixture} annotation is used to mark a class
 * as a dataset provider for a specific domain entity. This triggers the automatic generation of a
 * fixture class, making it easier to create and manipulate entities in tests.
 *
 * <p>
 * The {@code DataSetDefinition} interface defines the contract
 * that any dataset provider must follow, ensuring they provide a valid default data model and a method
 * to build the actual entity from the model.
 */

package io.github.romannbroque.fixture.annotations;