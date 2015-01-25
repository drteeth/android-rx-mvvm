package ca.unspace.timer.models;

import android.util.LongSparseArray;

public class UserRepo {
    private final LongSparseArray<User> users = new LongSparseArray<>();

    public UserRepo() {
        // simulate a user in the repo.
        User fakeUser = new User();
        fakeUser.id = 123L;
        fakeUser.name = "Ben Moss";
        save(fakeUser);
    }

    public void save(User user) {
        simulateWork();
        users.put(user.id, user);
    }

    public User find(long id) {
        simulateWork();
        return users.get(id);
    }

    private void simulateWork() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
