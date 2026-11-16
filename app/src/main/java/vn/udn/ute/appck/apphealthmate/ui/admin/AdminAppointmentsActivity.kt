package vn.udn.ute.appck.apphealthmate.ui.admin

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminAppointmentsBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAppointmentCardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository

/**
 * Màn hình Giám sát toàn bộ lịch hẹn trên hệ thống (dbo.LichHen).
 */
class AdminAppointmentsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminAppointmentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAppointmentsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        loadAllAppointments()
    }

    private fun loadAllAppointments() {
        binding.layoutAdminAppointments.removeAllViews()
        val list = AdminRepository.getAllAppointments()
        for (ap in list) {
            val apptCard = ItemAppointmentCardBinding.inflate(layoutInflater, binding.layoutAdminAppointments, false)
            apptCard.tvApptDateTime.text = "#${ap.maLichHen} • ${ap.ngayKham} (${ap.khungGio})"
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
            apptCard.tvApptTitle.text = "BS: ${ap.hocViBacSi} ${ap.tenBacSi} (Khoa ${ap.tenKhoa})"
            apptCard.tvApptSubtitle.text = "BN: ${ap.tenNguoiKham ?: ap.tenBenhNhan} (${ap.soDienThoaiBn}) • ${ap.tenPhong}"
            apptCard.tvApptNotes.text = "Triệu chứng: ${ap.trieuChung}"
            binding.layoutAdminAppointments.addView(apptCard.root)
        }
    }
}
