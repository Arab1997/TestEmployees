package my.testemployees.api;






import io.reactivex.Observable;
import my.testemployees.pojo.EmployeeResponse;
import retrofit2.http.GET;

public interface ApiService {
    @GET("testTask.json")
    Observable<EmployeeResponse> getEmployees(); // resultat zaprosa   Observable io.reactivex


    /*@GET("testTask.json/{id}/video")
    Observable<EmployeeResponse> getEmployees(@Path ("id") int id, @Query("api_key") int apiKey, @Query("lang") String lang); // resultat zaprosa   Observable io.reactivex
*/
}
