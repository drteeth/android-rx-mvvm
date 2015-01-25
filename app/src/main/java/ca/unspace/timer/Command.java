package ca.unspace.timer;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class Command<T> {

    private final Observable<T> observable;
    private final BehaviorSubject<Boolean> canExecute;

    public Command(Observable<T> observable) {
        this.observable = observable;
        this.canExecute = BehaviorSubject.create(true);
    }

    public Observable<T> execute() {
        return observable
            .doOnSubscribe(() -> canExecute.onNext(false))
            .doOnTerminate(() -> canExecute.onNext(true));
    }

    public Observable<Boolean> canExecute() {
        return canExecute;
    }
}
