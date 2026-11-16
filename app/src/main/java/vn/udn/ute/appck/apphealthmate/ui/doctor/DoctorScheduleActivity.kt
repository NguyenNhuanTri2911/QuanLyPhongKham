package vn.udn.ute.appck.apphealthmate.ui.doctor

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityDoctorScheduleBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.DoctorRepository
import vn.udn.ute.appck.apphealthmate.util.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Màn hình Quản lý lịch trực & Đăng ký ca trực Sáng/Chiều của Bác sĩ (dbo.LichTruc).
 */
class DoctorScheduleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.etShiftDate.setText(today)

        binding.btnSubmitShift.setOnClickListener {
            val dateStr = binding.etShiftDate.text?.toString().orEmpty().trim()
            if (dateStr.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập ngày trực!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val shift = if (binding.rbAfternoon.isChecked) "Chiều" else "Sáng"
            val docId = SessionManager.currentDoctorId ?: 1
            DoctorRepository.registerShift(docId, dateStr, shift, 15)
            Toast.makeText(this, "Đã gửi đăng ký ca trực chờ Admin duyệt!", Toast.LENGTH_LONG).show()
            loadSchedules()
        }

        loadSchedules()
    }

    private fun loadSchedules() {
        binding.layoutShiftList.removeAllViews()
        val docId = SessionManager.currentDoctorId ?: 1
        val list = DoctorRepository.getDoctorSchedules(docId)

        for (sc in list) {
            val shiftCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutShiftList, false)
            shiftCard.boxAvatar.setBackgroundResource(
                if (sc.trangThai) R.drawable.bg_squircle_green else R.drawable.bg_squircle_amber
            )
            shiftCard.tvAvatarText.text = sc.caTruc.take(1).uppercase()
            shiftCard.tvItemTitle.text = "Ngày trực: ${sc.ngayTruc} (Ca ${sc.caTruc})"
            shiftCard.tvItemSubtitle.text = "Khung giờ: ${sc.gioBatDau} - ${sc.gioKetThuc} • ${sc.tenPhong}"
            shiftCard.tvItemBadge.text = sc.trangThaiDuyet
            shiftCard.tvItemBadge.setBackgroundResource(
                if (sc.trangThai) R.drawable.bg_badge_green else R.drawable.bg_badge_amber
            )
            shiftCard.tvItemBadge.setTextColor(
                Color.parseColor(if (sc.trangThai) "#059669" else "#D97706")
            )
            shiftCard.tvItemDetails.text = "Số lượng bệnh nhân tối đa nhận khám: ${sc.soLuongToiDa} BN/ca"
            binding.layoutShiftList.addView(shiftCard.root)
        }
    }
}
