package vn.udn.ute.appck.apphealthmate.ui.admin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminRoomsBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository

/**
 * Màn hình Quản lý Phòng khám (Thêm / Xóa trên bảng dbo.Phong).
 */
class AdminRoomsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminRoomsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminRoomsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        binding.btnAddRoom.setOnClickListener { showAddRoomDialog() }

        loadRooms()
    }

    private fun loadRooms() {
        binding.layoutAdminRooms.removeAllViews()
        val rooms = AdminRepository.getAllRooms()
        for (room in rooms) {
            val itemCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAdminRooms, false)
            itemCard.boxAvatar.setBackgroundResource(R.drawable.bg_squircle_teal)
            itemCard.tvAvatarText.text = "P${room.maPhong}"
            itemCard.tvItemTitle.text = room.tenPhong
            itemCard.tvItemSubtitle.text = "Trực thuộc: Khoa ${room.tenKhoa}"
            itemCard.tvItemBadge.text = "Hoạt động"
            itemCard.tvItemBadge.setBackgroundResource(R.drawable.bg_badge_green)
            itemCard.tvItemBadge.setTextColor(Color.parseColor("#059669"))
            itemCard.tvItemDetails.text = "Vị trí phòng khám: ${room.viTri ?: "Tầng 1 - Khu Khám bệnh"}"
            itemCard.layoutActions.visibility = View.VISIBLE
            itemCard.btnPrimary.text = "Xóa phòng khám này"
            itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
            itemCard.btnPrimary.setTextColor(Color.parseColor("#DC2626"))
            itemCard.btnPrimary.setOnClickListener {
                AdminRepository.deleteRoom(room.maPhong)
                Toast.makeText(this, "Đã xóa phòng khám!", Toast.LENGTH_SHORT).show()
                loadRooms()
            }
            binding.layoutAdminRooms.addView(itemCard.root)
        }
    }

    private fun showAddRoomDialog() {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 24, 48, 8)
        }
        val etName = EditText(this).apply { hint = "Tên phòng khám (VD: Phòng Nội 105)" }
        val etLoc = EditText(this).apply { hint = "Vị trí (VD: Tầng 1 - Dãy B)" }
        container.addView(etName)
        container.addView(etLoc)

        MaterialAlertDialogBuilder(this)
            .setTitle("Thêm Phòng khám mới (dbo.Phong)")
            .setView(container)
            .setPositiveButton("Thêm") { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isEmpty()) return@setPositiveButton
                AdminRepository.addRoom(name, etLoc.text.toString().trim().ifEmpty { "Tầng 1" }, 1)
                Toast.makeText(this, "Đã thêm phòng khám!", Toast.LENGTH_SHORT).show()
                loadRooms()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
