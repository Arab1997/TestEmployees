package my.testemployees.screens.employees;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import my.testemployees.R;
import my.testemployees.adapters.EmployeeAdapter;
import my.testemployees.pojo.Employee;
import my.testemployees.pojo.Speciality;

public class EmployeeListActivity extends AppCompatActivity {
    private RecyclerView recyclerViewEmployees;
    private EmployeeAdapter adapter;
    private EmployeeViewModel viewModel;
  //  private EmployeeListPresenter presenter; // MVP
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    //    presenter = new EmployeeListPresenter(this); // MVP
        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        adapter = new EmployeeAdapter();
        adapter.setEmployees(new ArrayList<Employee>());
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmployees.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(EmployeeViewModel.class);
         viewModel.getEmployees().observe(this, new Observer<List<Employee>>() {//etot metod vozvrashaet LiveData  podpisatsiy izmeneniya  v bd
             @Override
             public void onChanged(List<Employee> employees) {

                 adapter.setEmployees(employees);
                 if (employees != null) {
                     for (Employee employee : employees) {
                         List<Speciality> specialities = employee.getSpecialty();
                         for (Speciality speciality : specialities) {
                             Log.i("Speciality", speciality.getName());
                         }
                     }
                 }
             }
         });
         viewModel.getErrors().observe(this, new Observer<Throwable>() { // mi podpisalis izmeneniya  v objecte LiveData s oshibkami
             @Override
             public void onChanged(Throwable throwable) {
                 if (throwable != null){
                 Toast.makeText(EmployeeListActivity.this, "Error", Toast.LENGTH_SHORT).show(); // yesli danniye izminilis
                 viewModel.clearErrors();  // ochishaem danniy
             }
             }
         });
         viewModel.loadData();  // zagrujaem data
    //    presenter.loadData();
    }




    // ------------------------  // MVP // -------------------------------------//
/*
    @Override
    protected void onDestroy() {
    //    presenter.disposeDisposable();    // MVP
        super.onDestroy();
    }

    @Override
    public void showData(List<Employee> employees) {
        adapter.setEmployees(employees);
    }

    @Override
    public void showError() {
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }*/
}
