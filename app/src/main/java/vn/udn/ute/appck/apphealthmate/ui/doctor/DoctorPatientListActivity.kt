package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorPatientListBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAppointmentCardBinding
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.util.SessionManager

/**
 * Màn hình Danh sách bệnh nhân đặt khám của Bác sĩ, lọc theo trạng thái.
 */
class DoctorPatientListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorPatientListBinding
    private var currentFilter = "WAITING"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorPatientListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnFilterWaiting.setOnClickListener { applyFilter("WAITING") }
        binding.btnFilterCompleted.setOnClickListener { applyFilter("COMPLETED") }
        binding.btnFilterAll.setOnClickListener { applyFilter("ALL") }
    }

    override fun onResume() {
        super.onResume()
        applyFilter(currentFilter)
    }

    private fun updateFilterButtons(active: String) {
        val map = mapOf(
            "WAITING" to binding.btnFilterWaiting,
            "COMPLETED" to binding.btnFilterCompleted,
            "ALL" to binding.btnFilterAll
        )
        for ((key, btn) in map) {
            if (key == active) {
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2563EB"))
                btn.setTextColor(Color.WHITE)
            } else {
                btn.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#EFF6FF"))
                btn.setTextColor(Color.parseColor("#1E40AF"))
            }
        }
    }

    private fun applyFilter(filter: String) {
        currentFilter = filter
        updateFilterButtons(filter)

        val docId = SessionManager.currentDoctorId ?: 1
        val all = DoctorRepository.getDoctorAppointments(docId)
        val list = when (filter) {
            "WAITING" -> all.filter { it.trangThai == "Sắp tới" || it.trangThai == "Đang chờ" }
            "COMPLETED" -> all.filter { it.trangThai == "Đã khám" }
            else -> all
        }

        binding.layoutPatientsContainer.removeAllViews()
        if (list.isEmpty()) {
            val tvEmpty = TextView(this).apply {
                text = "Không có bệnh nhân trong danh sách này."
                setTextColor(Color.parseColor("#64748B"))
                textSize = 14f
            }
            binding.layoutPatientsContainer.addView(tvEmpty)
            return
        }

        for (ap in list) {
            val apptCard = ItemAppointmentCardBinding.inflate(layoutInflater, binding.layoutPatientsContainer, false)
            apptCard.tvApptDateTime.text = "${ap.ngayKham} • ${ap.khungGio}"
            apptCard.tvApptStatusBadge.text = ap.trangThai
            when (ap.trangThai) {
                "Đã khám" -> {
                    apptCard.tvApptStatusBadge.setBackgroundResource(R.drawable.bg_badge_green)
                    apptCard.tvApptStatusBadge.setTextColor(Color.parseColor("#059669"))
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
            apptCard.btnApptAction.text = "Mở hồ sơ khám bệnh →"
            apptCard.btnApptAction.setOnClickListener {
                val intent = Intent(this, DoctorExamineActivity::class.java)
                intent.putExtra("EXTRA_APPOINTMENT_ID", ap.maLichHen)
                startActivity(intent)
            }
            binding.layoutPatientsContainer.addView(apptCard.root)
        }
    }
}
