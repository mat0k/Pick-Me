import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.pickme.data.model.PickUp
import com.example.pickme.data.repository.PickUpRepository
import com.example.pickme.notifications.PickupsNotificationService
import com.example.pickme.view.ui.driver.HomeScreenVM
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdatePickupsWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val pickUpRepository = PickUpRepository()
            val allPickups = withContext(Dispatchers.IO) {
                pickUpRepository.getLivePickUps().value ?: emptyList()
            }

            val homeScreenVM = HomeScreenVM(context)
            val filteredPickups = homeScreenVM.filterPickUps(context, allPickups)

            val existingPickups = homeScreenVM.pickUps.value ?: emptyList()
            val newPickups = filteredPickups.minus(existingPickups)

            if (newPickups.isNotEmpty()) {
                val notificationService = PickupsNotificationService(context)
                newPickups.forEach { newPickup ->
                    notificationService.showNotification(newPickup)
                }
            }

            val gson = Gson()
            val filteredPickupsJson = gson.toJson(filteredPickups)

            val outputData = workDataOf("filteredPickups" to filteredPickupsJson)

            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}