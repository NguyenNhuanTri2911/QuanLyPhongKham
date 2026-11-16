package vn.udn.ute.appck.apphealthmate.ui.admin

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import vn.udn.ute.appck.apphealthmate.R
import vn.udn.ute.appck.apphealthmate.databinding.ActivityAdminPatientsBinding
import vn.udn.ute.appck.apphealthmate.databinding.ItemAdminCardBinding
import vn.udn.ute.appck.apphealthmate.repository.AdminRepository

/**
 * Màn hình Quản lý Bệnh nhân & Khóa/Mở khóa tài khoản (dbo.NguoiDung).
 */
class AdminPatientsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminPatientsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPatientsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }
        loadPatients()
    }

    private fun loadPatients() {
        binding.layoutAdminPatients.removeAllViews()
        val patients = AdminRepository.getAllPatients()
        for (p in patients) {
            val isLocked = p.matKhau == "LOCKED"
            val itemCard = ItemAdminCardBinding.inflate(layoutInflater, binding.layoutAdminPatients, false)
            val initials = p.hoTen.trim().split(" ").takeLast(2).joinToString("") { it.take(1).uppercase() }
            itemCard.boxAvatar.setBackgroundResource(
                if (isLocked) R.drawable.bg_squircle_red else R.drawable.bg_squircle_blue
            )
            itemCard.tvAvatarText.text = initials.ifEmpty { "BN" }
            itemCard.tvItemTitle.text = p.hoTen
            itemCard.tvItemSubtitle.text = "SĐT định danh: ${p.soDienThoai}"
            itemCard.tvItemBadge.text = if (isLocked) "Đã khóa" else "Hoạt động"
            itemCard.tvItemBadge.setBackgroundResource(
                if (isLocked) R.drawable.bg_badge_red else R.drawable.bg_badge_green
            )
            itemCard.tvItemBadge.setTextColor(
                Color.parseColor(if (isLocked) "#DC2626" else "#059669")
            )
            itemCard.tvItemDetails.text = "Email: ${p.email ?: "Chưa cập nhật"} • Giới tính: ${p.gioiTinh}\nĐịa chỉ: ${p.diaChi ?: "Chưa cập nhật"}"
            itemCard.layoutActions.visibility = View.VISIBLE

            if (isLocked) {
                itemCard.btnPrimary.text = "Mở khóa tài khoản (Reset MK: 123456)"
                itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#ECFDF5"))
                itemCard.btnPrimary.setTextColor(Color.parseColor("#059669"))
            } else {
                itemCard.btnPrimary.text = "Khóa tài khoản bệnh nhân này"
                itemCard.btnPrimary.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FEF2F2"))
                itemCard.btnPrimary.setTextColor(Color.parseColor("#DC2626"))
            }

            itemCard.btnPrimary.setOnClickListener {
                AdminRepository.toggleUserLock(p.soDienThoai, !isLocked)
                Toast.makeText(this, "Đã cập nhật trạng thái tài khoản!", Toast.LENGTH_SHORT).show()
                loadPatients()
            }

            binding.layoutAdminPatients.addView(itemCard.root)
        }
    }
}
