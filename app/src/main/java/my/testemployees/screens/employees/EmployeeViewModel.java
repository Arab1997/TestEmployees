package my.testemployees.screens.employees;

import android.app.Application;

import android.os.AsyncTask;


import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;


import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import my.testemployees.api.ApiFactory;
import my.testemployees.api.ApiService;
import my.testemployees.data.AppDatabase;
import my.testemployees.pojo.Employee;
import my.testemployees.pojo.EmployeeResponse;
import rx.schedulers.Schedulers;

import static rx.schedulers.Schedulers.*;

public class EmployeeViewModel extends AndroidViewModel {

    private static AppDatabase db;
    private LiveData<List<Employee>> employees;
    private MutableLiveData<Throwable> errors;  // chtobi otobrajat oshibki

    private CompositeDisposable compositeDisposable;

    public EmployeeViewModel(@NonNull Application application) {
        super(application);
        db = AppDatabase.getInstance(application);
        employees = db.employeeDao().getAllEmployees();
        errors = new MutableLiveData<>();
    }

    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }

    public LiveData<Throwable> getErrors() {
        return errors;
    } // LiveData ne mojem izminit datu   MutableLiveData<> etom metodom mi mojem izminit

    public void clearErrors() {
        errors.setValue(null);
    } // shtobi error ne saxranilsa na ViewModele

    @SuppressWarnings("unchecked")
    private void insertEmployees(List<Employee> employees) {
        new InsertEmployeesTask().execute(employees);
    }

    private static class InsertEmployeesTask extends AsyncTask<List<Employee>, Void, Void> {
        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Employee>... lists) {
            if (lists != null && lists.length > 0) {
                db.employeeDao().insertEmployees(lists[0]);
            }
            return null;
        }
    }

    private void deleteAllEmployees() {
        new DeleteAllEmployeesTask().execute();
    }

    private static class DeleteAllEmployeesTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.employeeDao().deleteAllEmployees();
            return null;
        }
    }

    public void loadData() {
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = apiService.getEmployees()
                //  Disposable disposable = apiService.getEmployees(2323, "ru")
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())// kakom patoke vsyo delat
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new io.reactivex.functions.Consumer<EmployeeResponse>() {
                    @Override
                    public void accept(EmployeeResponse employeeResponse) throws Exception {
                        deleteAllEmployees();
                        insertEmployees(employeeResponse.getEmployees());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        errors.setValue(throwable);
                    }
                });
        compositeDisposable.add(disposable);
    }


    @Override
    protected void onCleared() { // pri unijtojeniya View Model
        compositeDisposable.dispose();
        super.onCleared();
    }
}
