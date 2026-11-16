package vn.udn.ute.appck.apphealthmate.ui.admin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminSchedulesBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.model.LichTruc
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository

/**
 * Màn hình Duyệt lịch trực Bác sĩ & Gán phòng khám trống (dbo.LichTruc).
 */
class AdminSchedulesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminSchedulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminSchedulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        loadSchedules()
    }

    private fun loadSchedules() {
        binding.layoutAdminSchedules.removeAllViews()
        val schedules = AdminRepository.getAllSchedules()
        for (sc in schedules) {
            val itemCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAdminSchedules, false)
            itemCard.boxAvatar.setBackgroundResource(
                if (sc.trangThai) R.drawable.bg_squircle_green else R.drawable.bg_squircle_amber
            )
            itemCard.tvAvatarText.text = sc.caTruc.take(1).uppercase()
            itemCard.tvItemTitle.text = "${sc.tenBacSi} (Khoa ${sc.tenKhoa})"
            itemCard.tvItemSubtitle.text = "Ngày: ${sc.ngayTruc} • Ca ${sc.caTruc} (${sc.gioBatDau}-${sc.gioKetThuc})"
            itemCard.tvItemBadge.text = sc.trangThaiDuyet
            itemCard.tvItemBadge.setBackgroundResource(
                if (sc.trangThai) R.drawable.bg_badge_green else R.drawable.bg_badge_amber
            )
            itemCard.tvItemBadge.setTextColor(
                Color.parseColor(if (sc.trangThai) "#059669" else "#D97706")
            )
            itemCard.tvItemDetails.text = "Phòng được phân công: ${sc.tenPhong} • Tối đa: ${sc.soLuongToiDa} BN"
            itemCard.layoutActions.visibility = View.VISIBLE
            itemCard.btnPrimary.text = "✓ Duyệt & Xếp phòng"
            itemCard.btnPrimary.setOnClickListener {
                showAssignRoomDialog(sc)
            }
            itemCard.btnSecondary.visibility = View.VISIBLE
            itemCard.btnSecondary.text = "✕ Từ chối"
            itemCard.btnSecondary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
            itemCard.btnSecondary.setTextColor(Color.parseColor("#DC2626"))
            itemCard.btnSecondary.setOnClickListener {
                AdminRepository.rejectSchedule(sc.maLichTruc)
                Toast.makeText(this, "Đã từ chối ca trực!", Toast.LENGTH_SHORT).show()
                loadSchedules()
            }
            binding.layoutAdminSchedules.addView(itemCard.root)
        }
    }

    private fun showAssignRoomDialog(sc: LichTruc) {
        val rooms = AdminRepository.getAllRooms()
        val roomNames = rooms.map { "${it.tenPhong} (${it.viTri})" }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle("Chọn Phòng khám cho ${sc.tenBacSi}")
            .setItems(roomNames) { _, which ->
                val chosenRoom = rooms[which]
                AdminRepository.approveScheduleWithRoom(sc.maLichTruc, chosenRoom.maPhong)
                Toast.makeText(
                    this,
                    "Đã duyệt ca trực và xếp vào ${chosenRoom.tenPhong}!",
                    Toast.LENGTH_LONG
                ).show()
                loadSchedules()
            }
            .show()
    }
}
