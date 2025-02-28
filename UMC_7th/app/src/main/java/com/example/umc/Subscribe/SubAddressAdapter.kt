package com.example.umc.Subscribe

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.umc.model.Address
import com.example.umc.R
import com.example.umc.Subscribe.Repository.AddressRepository
import com.example.umc.Subscribe.Repository.DeliveryAddressRepository
import com.example.umc.Subscribe.SubscribeRequest.DeliveryAddressputRequest
import com.example.umc.Subscribe.SubscribeResponse.Get.DeliveryGetResponse
import com.example.umc.UserApi.SharedPreferencesManager
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddressAdapter(
    private val context: Context,  // context 추가
    private var addressList: MutableList<Address>
) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        val nameTextView: TextView = itemView.findViewById(R.id.tv_name)
        val nameEditText: EditText = itemView.findViewById(R.id.et_name)
        val postcodeTextView: TextView = itemView.findViewById(R.id.tv_postcode)
        val postcodeEditText: EditText = itemView.findViewById(R.id.et_postcode)
        val addressTextView: TextView = itemView.findViewById(R.id.tv_address)
        val addressEditText: EditText = itemView.findViewById(R.id.et_address)
        val phoneTextView: TextView = itemView.findViewById(R.id.tv_phone)
        val phoneEditText: EditText = itemView.findViewById(R.id.et_phone)
        val memoTextView: TextView = itemView.findViewById(R.id.tv_memo)
        val memoEditText: EditText = itemView.findViewById(R.id.et_memo)
        val editSaveButton: Button = itemView.findViewById(R.id.btn_edit)
        val ivCheck: ImageButton = itemView.findViewById(R.id.iv_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_address, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addressList[position]
        holder.nameTextView.text = address.name
        holder.nameEditText.setText(address.name)
        holder.postcodeTextView.text = address.postcode
        holder.postcodeEditText.setText(address.postcode)
        holder.addressTextView.text = address.address
        holder.addressEditText.setText(address.address)
        holder.phoneTextView.text = address.phone
        holder.phoneEditText.setText(address.phone)
        holder.memoTextView.text = address.memo
        holder.memoEditText.setText(address.memo)

        val savedAddress = AddressRepository.getDefaultAddress(context)

        if (savedAddress != null && savedAddress.address == address.address) {
            holder.ivCheck.setImageResource(R.drawable.ic_orange_check)
            holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Primary_Orange1)
            holder.ivCheck.tag = "checked"
        } else {
            holder.ivCheck.setImageResource(R.drawable.ic_gray_check)
            holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Gray7)
            holder.ivCheck.tag = "unchecked"
        }

        // EditText 숨기기  (원래 코드)
//        holder.nameEditText.visibility = View.GONE
//        holder.postcodeEditText.visibility = View.GONE
//        holder.addressEditText.visibility = View.GONE
//        holder.phoneEditText.visibility = View.GONE
//        holder.memoEditText.visibility = View.GONE
        listOf(
            holder.nameEditText,
            holder.postcodeEditText,
            holder.addressEditText,
            holder.phoneEditText,
            holder.memoEditText
        ).forEach { it.visibility = View.GONE }

//        holder.editSaveButton.setOnClickListener {
//            if (holder.editSaveButton.text == "수정") {
//                // EditText 보이기
//                holder.nameEditText.visibility = View.VISIBLE
//                holder.postcodeEditText.visibility = View.VISIBLE
//                holder.addressEditText.visibility = View.VISIBLE
//                holder.phoneEditText.visibility = View.VISIBLE
//                holder.memoEditText.visibility = View.VISIBLE
//
//                // TextView 숨기기
//                holder.nameTextView.visibility = View.GONE
//                holder.postcodeTextView.visibility = View.GONE
//                holder.addressTextView.visibility = View.GONE
//                holder.phoneTextView.visibility = View.GONE
//                holder.memoTextView.visibility = View.GONE
//
//                // 버튼 텍스트 "저장"으로 변경
//                holder.editSaveButton.text = "저장"
//                holder.editSaveButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.Primary_Orange1))
//            } else {
//                // 입력된 내용 반영
//                address.name = holder.nameEditText.text.toString()
//                address.postcode = holder.postcodeEditText.text.toString()
//                address.address = holder.addressEditText.text.toString()
//                address.phone = holder.phoneEditText.text.toString()
//                address.memo = holder.memoEditText.text.toString()
//
//                // TextView 업데이트
//                holder.nameTextView.text = address.name
//                holder.postcodeTextView.text = address.postcode
//                holder.addressTextView.text = address.address
//                holder.phoneTextView.text = address.phone
//                holder.memoTextView.text = address.memo
//
//                // EditText 숨기기
//                holder.nameEditText.visibility = View.GONE
//                holder.postcodeEditText.visibility = View.GONE
//                holder.addressEditText.visibility = View.GONE
//                holder.phoneEditText.visibility = View.GONE
//                holder.memoEditText.visibility = View.GONE
//
//                // TextView 보이기
//                holder.nameTextView.visibility = View.VISIBLE
//                holder.postcodeTextView.visibility = View.VISIBLE
//                holder.addressTextView.visibility = View.VISIBLE
//                holder.phoneTextView.visibility = View.VISIBLE
//                holder.memoTextView.visibility = View.VISIBLE
//
//                // 버튼 텍스트 "수정"으로 변경
//                holder.editSaveButton.text = "수정"
//                holder.editSaveButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.Gray7))
//            }
//        } 원래코드

        // holder.editSaveButton.setOnClickListener
        holder.editSaveButton.setOnClickListener {
            if (holder.editSaveButton.text == "수정") {
                // 수정 모드 활성화
                listOf(
                    holder.nameEditText,
                    holder.postcodeEditText,
                    holder.addressEditText,
                    holder.phoneEditText,
                    holder.memoEditText
                ).forEach { it.visibility = View.VISIBLE }

                listOf(
                    holder.nameTextView,
                    holder.postcodeTextView,
                    holder.addressTextView,
                    holder.phoneTextView,
                    holder.memoTextView
                ).forEach { it.visibility = View.GONE }

                holder.editSaveButton.text = "저장"
                holder.editSaveButton.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.Primary_Orange1
                    )
                )
            } else {
                // 저장소에서 addressId 가져오기
                val addressIdFromStorage = AddressRepository.getAddressId(context)  // context 전달

                if (addressIdFromStorage == null) {
                    Toast.makeText(context, "주소 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // 업데이트할 데이터 생성
                val updatedAddress = DeliveryAddressputRequest(
                    addressId = addressIdFromStorage,
                    name = holder.nameEditText.text.toString(),
                    address = holder.addressEditText.text.toString(),
                    postNum = holder.postcodeEditText.text.toString()
                        .replace("[^0-9]".toRegex(), "").toInt(),
                    phoneNum = holder.phoneEditText.text.toString(),
                    memo = holder.memoEditText.text.toString()
                )

                // PUT 요청 실행
                CoroutineScope(Dispatchers.IO).launch {
                    val result = DeliveryAddressRepository.Put.updateDeliveryAddress(
                        context, addressIdFromStorage, updatedAddress
                    )

                    withContext(Dispatchers.Main) {
                        if (result.isSuccess) {
                            Toast.makeText(context, "주소 수정 성공", Toast.LENGTH_SHORT).show()

                            // UI 갱신
                            address.name = updatedAddress.name
                            address.postcode = updatedAddress.postNum.toString()
                            address.address = updatedAddress.address
                            address.phone = updatedAddress.phoneNum
                            address.memo = updatedAddress.memo

                            holder.nameTextView.text = address.name
                            holder.postcodeTextView.text = address.postcode
                            holder.addressTextView.text = address.address
                            holder.phoneTextView.text = address.phone
                            holder.memoTextView.text = address.memo
                        } else {
                            Toast.makeText(context, "주소 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                // EditText 숨기기 및 TextView 보이기 처리  원래코드
//                holder.nameEditText.visibility = View.GONE
//                holder.postcodeEditText.visibility = View.GONE
//                holder.addressEditText.visibility = View.GONE
//                holder.phoneEditText.visibility = View.GONE
//                holder.memoEditText.visibility = View.GONE
//                holder.nameTextView.visibility = View.VISIBLE
//                holder.postcodeTextView.visibility = View.VISIBLE
//                holder.addressTextView.visibility = View.VISIBLE
//                holder.phoneTextView.visibility = View.VISIBLE
//                holder.memoTextView.visibility = View.VISIBLE
//                holder.editSaveButton.text = "수정"
//                holder.editSaveButton.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.Gray7))
                listOf(
                    holder.nameEditText,
                    holder.postcodeEditText,
                    holder.addressEditText,
                    holder.phoneEditText,
                    holder.memoEditText
                ).forEach { it.visibility = View.GONE }

                listOf(
                    holder.nameTextView,
                    holder.postcodeTextView,
                    holder.addressTextView,
                    holder.phoneTextView,
                    holder.memoTextView
                ).forEach { it.visibility = View.VISIBLE }

                holder.editSaveButton.text = "수정"
                holder.editSaveButton.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.Gray7
                    )
                )

            }
        }


//        holder.ivCheck.setOnClickListener {
//            val addressId = AddressRepository.getAddressId(context)?.toIntOrNull()
//
//            if (addressId == null) {
//                Toast.makeText(context, "기본 배송지 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            if (holder.ivCheck.tag == "unchecked") {
//                holder.ivCheck.setImageResource(R.drawable.ic_orange_check)
//                holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Primary_Orange1)
//                holder.ivCheck.tag = "checked"
//
//                // 🚀 API 요청 (기본 배송지 설정)
//                CoroutineScope(Dispatchers.IO).launch {
//                    val result = DeliveryAddressRepository.Patch.setDefaultDeliveryAddress(context, addressId)
//
//                    withContext(Dispatchers.Main) {
//                        if (result.isSuccess) {
//                            Toast.makeText(context, "기본 배송지가 설정되었습니다.", Toast.LENGTH_SHORT).show()
//
//// 🚀 SharedPreferences에서 userId 가져오기
//                            val storedUserId = SharedPreferencesManager.getUserId(context)
//
//// 🚀 SharedPreferences에서 addressId 가져오기
//                            val storedAddressId = AddressRepository.getAddressId(context)?.toIntOrNull()
//
//                            if (storedUserId == -1 || storedAddressId == null) {
//                                Toast.makeText(context, "userId 또는 addressId를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
//                                return@withContext
//                            }
//
//// 🚀 기본 배송지 정보를 포함한 객체 생성
//                            val defaultAddress = DeliveryGetResponse(
//                                addressId = storedAddressId,  // ✅ addressId 추가
//                                userId = storedUserId,  // ✅ userId 추가
//                                isDefault = true,  // ✅ 기본 배송지 설정
//                                name = address.name,
//                                address = address.address,
//                                postNum = address.postcode.replace("[^0-9]".toRegex(), "").toIntOrNull() ?: 0, // 숫자로 변환, 기본값 0
//                                phoneNum = address.phone,
//                                memo = address.memo
//                            )
//
//// ✅ SharedPreferences에 기본 배송지 저장
//                            AddressRepository.saveDefaultAddress(context, defaultAddress)
//
//
//
//
//                            // ✅ UI 업데이트 (다른 체크 해제)
//                            notifyDataSetChanged()
//                        } else {
//                            Toast.makeText(context, "기본 배송지 설정 실패", Toast.LENGTH_SHORT).show()
//                            holder.ivCheck.setImageResource(R.drawable.ic_gray_check)
//                            holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Gray7)
//                            holder.ivCheck.tag = "unchecked"
//                        }
//                    }
//                }
//            } else {
//                holder.ivCheck.setImageResource(R.drawable.ic_gray_check)
//                holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Gray7)
//                holder.ivCheck.tag = "unchecked"
//            }
//        }
        // 이것이 원래 코드
        holder.ivCheck.setOnClickListener {
            // 🚀 현재 클릭한 주소 정보를 가져옴
            val clickedAddress = addressList[position]

            // 🚀 저장된 기본 배송지 ID 확인, 없으면 클릭한 주소의 addressId 사용
            val addressId = AddressRepository.getAddressId(context)?.toIntOrNull()
                ?: (clickedAddress as? DeliveryGetResponse)?.addressId

            if (addressId == null) {
                Toast.makeText(context, "기본 배송지 ID를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (holder.ivCheck.tag == "unchecked") {
                holder.ivCheck.setImageResource(R.drawable.ic_orange_check)
                holder.cardView.strokeColor =
                    ContextCompat.getColor(context, R.color.Primary_Orange1)
                holder.ivCheck.tag = "checked"

                // 🚀 API 요청 (기본 배송지 설정)
                CoroutineScope(Dispatchers.IO).launch {
                    val result = DeliveryAddressRepository.Patch.setDefaultDeliveryAddress(
                        context,
                        addressId
                    )

                    withContext(Dispatchers.Main) {
                        if (result.isSuccess) {
                            Toast.makeText(context, "기본 배송지가 설정되었습니다.", Toast.LENGTH_SHORT).show()

                            // 🚀 SharedPreferences에서 userId 가져오기
                            val storedUserId = SharedPreferencesManager.getUserId(context)

                            // 🚀 기본 배송지 정보를 포함한 객체 생성
                            val defaultAddress = DeliveryGetResponse(
                                addressId = addressId,
                                userId = storedUserId,
                                isDefault = true, // ✅ 기본 배송지 설정
                                name = clickedAddress.name,
                                address = clickedAddress.address,
                                postNum = clickedAddress.postcode.replace("[^0-9]".toRegex(), "")
                                    .toIntOrNull() ?: 0,
                                phoneNum = clickedAddress.phone,
                                memo = clickedAddress.memo
                            )

                            // ✅ SharedPreferences에 기본 배송지 저장
                            AddressRepository.saveDefaultAddress(context, defaultAddress)

                            // ✅ 기존 기본 배송지가 있으면 체크 해제
                            // 🚀 기존 기본 배송지가 있으면 체크 해제
                            addressList.forEachIndexed { index, item ->
                                val deliveryItem = item as? DeliveryGetResponse // 안전한 타입 변환
                                if (deliveryItem != null && deliveryItem.addressId != addressId) {
                                    notifyItemChanged(index)
                                }
                            }


                            // ✅ UI 업데이트
                            notifyDataSetChanged()
                        } else {
                            Toast.makeText(context, "기본 배송지 설정 실패", Toast.LENGTH_SHORT).show()
                            holder.ivCheck.setImageResource(R.drawable.ic_gray_check)
                            holder.cardView.strokeColor =
                                ContextCompat.getColor(context, R.color.Gray7)
                            holder.ivCheck.tag = "unchecked"
                        }
                    }
                }
            } else {
                holder.ivCheck.setImageResource(R.drawable.ic_gray_check)
                holder.cardView.strokeColor = ContextCompat.getColor(context, R.color.Gray7)
                holder.ivCheck.tag = "unchecked"
            }
        }


    }override fun getItemCount() = addressList.size
}
