package io.github.susimsek.springaisamples.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("java:S112")
public class ThrowableUtils {

    @FunctionalInterface
    public interface ThrowingSupplier<T, X extends Throwable> extends Supplier<T> {
        T tryGet() throws X;

        @Override
        @SneakyThrows
        default T get() {
            return tryGet();
        }
    }

    @FunctionalInterface
    public interface ThrowingFunction<T, R, X extends Throwable> extends Function<T, R> {
        R tryApply(T t) throws X;

        @Override
        @SneakyThrows
        default R apply(T t) {
            return tryApply(t);
        }
    }

    @FunctionalInterface
    public interface ThrowingConsumer<T, X extends Throwable> extends Consumer<T> {
        void tryAccept(T t) throws X;

        @Override
        @SneakyThrows
        default void accept(T t) {
            tryAccept(t);
        }
    }

    @FunctionalInterface
    public interface ThrowingRunnable<X extends Throwable> extends Runnable {
        void tryRun() throws X;

        @Override
        @SneakyThrows
        default void run() {
            tryRun();
        }
    }

    @FunctionalInterface
    public interface ThrowingPredicate<T, X extends Throwable> extends Predicate<T> {
        boolean tryTest(T t) throws X;

        @Override
        @SneakyThrows
        default boolean test(T t) {
            return tryTest(t);
        }
    }

    public static <T, X extends Throwable> Supplier<T> throwingSupplier(ThrowingSupplier<T, X> throwingSupplier) {
        return throwingSupplier;
    }

    public static <T, R, X extends Throwable> Function<T, R> throwingFunction(ThrowingFunction<T, R, X> throwingFunction) {
        return throwingFunction;
    }

    public static <T, X extends Throwable> Consumer<T> throwingConsumer(ThrowingConsumer<T, X> throwingConsumer) {
        return throwingConsumer;
    }

    public static <X extends Throwable> Runnable throwingRunnable(ThrowingRunnable<X> throwingRunnable) {
        return throwingRunnable;
    }

    public static <T, X extends Throwable> Predicate<T> throwingPredicate(ThrowingPredicate<T, X> throwingPredicate) {
        return throwingPredicate;
    }
}