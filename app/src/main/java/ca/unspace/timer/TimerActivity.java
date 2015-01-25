package ca.unspace.timer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ca.unspace.timer.models.UserRepo;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static rx.android.app.AppObservable.bindActivity;

public class TimerActivity extends Activity {
    private final CompositeSubscription subscription = new CompositeSubscription();

    private TextView usernameView;
    private TextView timerView;
    private Button startButton;
    private Button stopButton;
    private TextView statusView;
    private ViewModel timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        usernameView = (TextView) findViewById(R.id.username);
        timerView = (TextView) findViewById(R.id.timer);
        startButton = (Button) findViewById(R.id.start);
        stopButton = (Button) findViewById(R.id.stop);
        statusView = (TextView) findViewById(R.id.status);

        timer = new ViewModel(new UserRepo(), 123L);
    }

    @Override protected void onResume() {
        super.onResume();

        subscribe(timer.username(), name -> usernameView.setText(name));
        subscribe(timer.timerText(), text -> timerView.setText(text));
        subscribe(timer.status(), status -> statusView.setText(status));

        subscribe(timer.canStart(), b -> startButton.setEnabled(b));
        startButton.setOnClickListener(v -> subscribe(timer.start()));

        subscribe(timer.canStop(), b -> stopButton.setEnabled(b));
        stopButton.setOnClickListener(v -> subscribe(timer.stop()));
    }

    @Override protected void onPause() {
        subscription.clear();
        super.onPause();
    }

    private <T> void subscribe(Observable<T> observable) {
        subscribe(observable, t -> {
        });
    }

    private <T> void subscribe(Observable<T> observable, Action1<? super T> onNext) {
        final Context context = this;
        subscription.add(bindActivity(this, observable.subscribeOn(Schedulers.io())).subscribe(onNext,
            throwable -> Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show()));
    }

}
