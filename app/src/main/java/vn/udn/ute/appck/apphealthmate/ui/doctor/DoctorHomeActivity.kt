package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorHomeBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAppointmentCardBinding
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.repository.PatientRepository
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Trang chủ Bác sĩ + Thống kê ca khám trong ngày + Danh sách bệnh nhân chờ khám nhanh.
 */
class DoctorHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnManageSchedule.setOnClickListener {
            startActivity(Intent(this, DoctorScheduleActivity::class.java))
        }
        binding.btnPatientList.setOnClickListener {
            startActivity(Intent(this, DoctorPatientListActivity::class.java))
        }
        binding.btnDoctorProfile.setOnClickListener {
            startActivity(Intent(this, DoctorProfileActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadDoctorDashboard()
    }

    private fun loadDoctorDashboard() {
        val docId = SessionManager.currentDoctorId ?: 1
        val doc = PatientRepository.getDoctorById(docId)
        binding.tvDoctorGreeting.text = doc?.let { "${it.hocVi} ${it.hoTen}" } ?: SessionManager.currentName

        val (shifts, waiting, completed) = DoctorRepository.getDailyStats(docId)
        binding.tvStatShifts.text = shifts.toString()
        binding.tvStatWaiting.text = waiting.toString()
        binding.tvStatCompleted.text = completed.toString()

        binding.layoutTodayPatients.removeAllViews()
        val list = DoctorRepository.getDoctorAppointments(docId)
        if (list.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Hiện chưa có bệnh nhân nào đặt lịch khám."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
            }
            binding.layoutTodayPatients.addView(tvEmpty)
            return
        }

        for (ap in list) {
            val apptCard = ItemAppointmentCardBinding.inflate(layoutInflater, binding.layoutTodayPatients, false)
            apptCard.tvApptDateTime.text = "${ap.ngayKham} • ${ap.khungGio}"
            apptCard.tvApptStatusBadge.text = ap.trangThai
            when (ap.trangThai) {
                "Đã khám" -> {
                    apptCard.tvApptStatusBadge.setBackgroundResource(R.drawable.bg_badge_green)
                    apptCard.tvApptStatusBadge.setTextColor(Color.parseColor("#059669"))
                }
                "Đã hủy", "Đã vắng" -> {
                    apptCard.tvApptStatusBadge.setBackgroundResource(R.drawable.bg_badge_red)
                    apptCard.tvApptStatusBadge.setTextColor(Color.parseColor("#DC2626"))
                }
                else -> {
                    apptCard.tvApptStatusBadge.setBackgroundResource(R.drawable.bg_badge_blue)
                    apptCard.tvApptStatusBadge.setTextColor(Color.parseColor("#1E40AF"))
                }
            }

            apptCard.tvApptTitle.text = "BN: ${ap.tenNguoiKham ?: ap.tenBenhNhan}"
            apptCard.tvApptSubtitle.text = "SĐT: ${ap.soDienThoaiBn} • ${ap.tenPhong}"
            apptCard.tvApptNotes.text = "Triệu chứng: ${ap.trieuChung}"
            apptCard.btnApptAction.visibility = View.VISIBLE
            apptCard.btnApptAction.text = "Mở hồ sơ khám & Kê đơn thuốc →"
            apptCard.btnApptAction.setOnClickListener {
                val intent = Intent(this, DoctorExamineActivity::class.java)
                intent.putExtra("EXTRA_APPOINTMENT_ID", ap.maLichHen)
                startActivity(intent)
            }
            binding.layoutTodayPatients.addView(apptCard.root)
        }
    }
}
