package vn.udn.ute.appck.apphealthmate.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminDashboardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository
import vn.udn.ute.appck.apphealthmate.ui.auth.LoginActivity
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Màn hình Tổng quan 6 chỉ số hệ thống (Admin Dashboard) & Điều hướng 6 module quản trị.
 */
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoDoctors.setOnClickListener {
            startActivity(Intent(this, AdminDoctorsActivity::class.java))
        }
        binding.btnGoPatients.setOnClickListener {
            startActivity(Intent(this, AdminPatientsActivity::class.java))
        }
        binding.btnGoDepartments.setOnClickListener {
            startActivity(Intent(this, AdminDepartmentsActivity::class.java))
        }
        binding.btnGoRooms.setOnClickListener {
            startActivity(Intent(this, AdminRoomsActivity::class.java))
        }
        binding.btnGoSchedules.setOnClickListener {
            startActivity(Intent(this, AdminSchedulesActivity::class.java))
        }
        binding.btnGoAppointments.setOnClickListener {
            startActivity(Intent(this, AdminAppointmentsActivity::class.java))
        }

        binding.btnAdminLogout.setOnClickListener {
            SessionManager.logout()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val stats = AdminRepository.getDashboardStats()
        binding.tvKpiSummary.text =
            "📊 THỐNG KÊ HỆ THỐNG (HEALTHMATEDB):\n" +
            "• Tổng số Bác sĩ: ${stats.totalDoctors}   |   • Tổng số Bệnh nhân: ${stats.totalPatients}\n" +
            "• Số Chuyên khoa: ${stats.totalDepartments}   |   • Số Phòng khám: ${stats.totalRooms}\n" +
            "• Ca trực chờ duyệt: ${stats.pendingSchedules}   |   • Tổng Lịch hẹn: ${stats.totalAppointments}"
    }
}

