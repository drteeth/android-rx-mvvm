package ca.unspace.timer;

import java.util.concurrent.TimeUnit;

import ca.unspace.timer.models.Timer;
import ca.unspace.timer.models.User;
import ca.unspace.timer.models.UserRepo;
import rx.Observable;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

public class ViewModel {
    private final Observable<User> user;
    private final Timer timer = new Timer();
    private final BehaviorSubject<Boolean> isRunning = BehaviorSubject.create(false);
    private final BehaviorSubject<String> status = BehaviorSubject.create();
    private final Command<Boolean> startCommand = new Command<>(startTimer());
    private final Command<Boolean> stopCommand = new Command<>(stopTimer());
    private final Func1<User, String> nameFromUser = u -> u.name;

    public ViewModel(UserRepo userRepo, long userId) {
        this.user = createUserObservable(userRepo, userId);
    }

    public Observable<String> username() {
        return user.map(nameFromUser);
    }

    private Observable<User> createUserObservable(final UserRepo userRepo, final long userId) {
        return Observable.create(subscriber -> {
            User u = userRepo.find(userId);
            subscriber.onNext(u);
            subscriber.onCompleted();
        });
    }

    public Observable<String> timerText() {
        Observable<Long> interval = Observable.interval(1, TimeUnit.SECONDS);
        Observable<Long> runningInterval = interval.filter(tick -> timer.isRunning());
        return runningInterval.map(tick -> {
            long elapsed = timer.getMillis() / 1000;
            long minutes = elapsed / 60;
            long seconds = elapsed % 60;
            return String.format("%02d:%02d", minutes, seconds);
        });
    }

    public Observable<Boolean> start() {
        return startCommand.execute()
            .doOnSubscribe(() -> status.onNext("Starting..."))
            .doOnTerminate(() -> status.onNext("Started."));
    }

    public Observable<Boolean> stop() {
        return stopCommand.execute()
            .doOnSubscribe(() -> status.onNext("Stopping..."))
            .doOnTerminate(() -> status.onNext("Stopped."));
    }

    public Observable<Boolean> canStart() {
        return Observable.combineLatest(isRunning, startCommand.canExecute(),
            (running, canExecute) -> !running && canExecute);
    }

    public Observable<Boolean> canStop() {
        return Observable.combineLatest(isRunning, stopCommand.canExecute(),
            (running, canExecute) -> running && canExecute);
    }

    public Observable<String> status() {
        return status;
    }

    private Observable<Boolean> startTimer() {
        return Observable.create(subscriber -> {
            boolean running = timer.start();
            subscriber.onNext(running);
            isRunning.onNext(running);
            subscriber.onCompleted();
        });
    }

    private Observable<Boolean> stopTimer() {
        return Observable.create(subscriber -> {
            boolean running = timer.stop();
            subscriber.onNext(running);
            isRunning.onNext(running);
            subscriber.onCompleted();
        });
    }
}
