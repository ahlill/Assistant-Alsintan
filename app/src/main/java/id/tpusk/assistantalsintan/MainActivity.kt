package id.tpusk.assistantalsintan

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import id.tpusk.assistantalsintan.databinding.ActivityMainBinding
import kotlin.math.ceil

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initLinearLayout()
        with(binding) {
            btnAddCurrentTractor.setOnClickListener {
                addView(
                    llCurrentTractor,
                    tvDescriptionCurrentTractor,
                    R.layout.field_layout_current_tractor
                )
            }
            btnMinCurrentTractor.setOnClickListener {
                deleteView(
                    llCurrentTractor,
                    tvDescriptionCurrentTractor
                )
            }
            btnAddOptionalTractor.setOnClickListener {
                addView(
                    llOptionalTractor,
                    tvDescriptionOptionalTractor,
                    R.layout.field_layout_optional_tractor
                )
            }
            btnMinOptionalTractor.setOnClickListener {
                deleteView(
                    llOptionalTractor,
                    tvDescriptionOptionalTractor
                )
            }

            btnRun.setOnClickListener {
                if (etArea.validate() && etMasaOlahLahan.validate()) {
                    try {
                        val luasLahan = etArea.text.toString().toDouble()//1000 //Ha
                        val masaOlahLahan =
                            etMasaOlahLahan.text.toString().toDouble() //20 // jam/musim tanam
//        val dataTraktorSaatIni = arrayListOf(DataCurrentTractor("data sekarang satu", 3.0, 2.0), DataCurrentTractor("data sekarang dua", 2.0, 1.0), DataCurrentTractor("data sekarang tiga", 1.5, 3.0), )
                        val dataTraktorSaatIni =
                            getDataFieldFromCurrentTractor(binding.llCurrentTractor)

//        val dataTraktorPilihan = arrayListOf(DataOptionalTractor("nama pilihan satu", 2300.0, 2.0), DataOptionalTractor("nama pilihan dua", 1300.0, 1.0), DataOptionalTractor("nama pilihan tiga", 2000.0, 1.0))
                        val dataTraktorPilihan =
                            getDataFieldFromOptionalTractor(binding.llOptionalTractor)

// proses
                        val sigmaKapasitasKerjaTraktorSaatIni: Double =
                            dataTraktorSaatIni.sumOf { it.capacity * it.unit } // penjumlahan kapasitas kerja seluruh traktor yang tersedia
                        val luasLahanYangSudahDiKerjakan =
                            masaOlahLahan * sigmaKapasitasKerjaTraktorSaatIni
                        val luasLahanYangBelumDiKerjakan =
                            luasLahan - luasLahanYangSudahDiKerjakan // lahan yang tidak dapat dikerjakan traktor saat ini

                        val apakahHarusMembeli: Boolean =
                            masaOlahLahan * sigmaKapasitasKerjaTraktorSaatIni <= luasLahan // kemampuan olah lahan dari traktor selama periode olah lahan harus lebih besar dari luas lahan

                        val dataTraktorRekomendasi = dataTraktorPilihan.minBy { it.eff }
                        val namaTraktorRekomendasi = dataTraktorRekomendasi.name
                        val kapasitasKerjaTraktorRekomendasi = dataTraktorRekomendasi.capacity
                        val hargaTraktorRekomendasi = dataTraktorRekomendasi.price // Rp/unit
                        val jumlahTraktorRekomendasi =
                            ceil(luasLahanYangBelumDiKerjakan / kapasitasKerjaTraktorRekomendasi / masaOlahLahan)

// output
                        val jumlahTraktorYangHarusDiBeli =
                            jumlahTraktorRekomendasi // jumlah traktor yang harus di beli
                        val jenisTraktorYangHarusDiBeli = namaTraktorRekomendasi
                        val biayaPembelian = hargaTraktorRekomendasi * jumlahTraktorRekomendasi
                        val luasLahanYangDiKerjakanTraktorBaru =
                            jumlahTraktorYangHarusDiBeli * kapasitasKerjaTraktorRekomendasi * masaOlahLahan


                        if (apakahHarusMembeli) {
                            val dataResult = DataResult(
                                namaTraktorRekomendasi,
                                jumlahTraktorRekomendasi,
                                biayaPembelian,
                                luasLahanYangDiKerjakanTraktorBaru
                            )
                            val text =
                                "Luas lahan total: $luasLahan Ha\n" +
                                        "Luas lahan yang dapat dikerjakan: $luasLahanYangSudahDiKerjakan Ha\n" +
                                        "Luas lahan yang belum dikerjakan: $luasLahanYangBelumDiKerjakan Ha\n\n" +
                                        "Untuk dapat menyelesaikan areal seluas $luasLahan Ha dengan tenggat waktu $masaOlahLahan Jam, maka di butuhkan tambahan alat, yaitu:\n" +
                                "jumlah traktor yang harus di beli: $jumlahTraktorYangHarusDiBeli Unit\n" +
                                        "Jenis traktor yang harus di beli: $jenisTraktorYangHarusDiBeli\n" +
                                        "biaya pembelian: Rp$biayaPembelian\n" +
                                        "luas lahan yang di kerjakan traktor baru: $luasLahanYangDiKerjakanTraktorBaru Ha\n"
                            tvDescriptionResult.text = text
                        } else {
                            val text =
                                "Kamu tidak harus membeli traktor tambahan, karena traktor yang tersedia sudah mencukupi"
                            Log.d(TAG, text)
                            tvDescriptionResult.text = text
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this@MainActivity, "Isi Data Traktor", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun addView(field: LinearLayout, tv: TextView?, view: Int) {
        var v: View?
        val inflater = LayoutInflater.from(this).inflate(view, null)
        field.addView(inflater, field.childCount)
        tv?.visibility = View.VISIBLE
    }

    private fun deleteView(field: LinearLayout, tv: TextView?) {
        val count = field.childCount
        if (count > 0) {
            val v: View? = field.getChildAt(count - 1)
            field.removeView(v)
        }
        if (count == 1) tv?.visibility = View.GONE
    }

    private fun getDataFieldFromCurrentTractor(field: LinearLayout): ArrayList<DataCurrentTractor> {
        val dataList = ArrayList<DataCurrentTractor>()

        val count = field.childCount
        var v: View?

        for (i in 0 until count) {
            v = field.getChildAt(i)

            val name = v.findViewById<EditText?>(R.id.et_name)
            val unit = v.findViewById<EditText?>(R.id.et_unit)
            val capacity = v.findViewById<EditText?>(R.id.et_capacity)

            val data = DataCurrentTractor(
                name.str(),
                unit.str().toDouble(),
                capacity.str().toDouble()
            )

            dataList.add(data)
        }
        return dataList
    }

    private fun getDataFieldFromOptionalTractor(field: LinearLayout): ArrayList<DataOptionalTractor> {
        val dataList = ArrayList<DataOptionalTractor>()

        val count = field.childCount
        var v: View?

        for (i in 0 until count) {
            v = field.getChildAt(i)

            val name = v.findViewById<EditText?>(R.id.et_name).text.toString()
            val price = v.findViewById<EditText?>(R.id.et_prica).text.toString().toDouble()
            val capacity = v.findViewById<EditText?>(R.id.et_capacity).text.toString().toDouble()

            val data = DataOptionalTractor(
                name,
                price,
                capacity
            )

            dataList.add(data)
        }
        return dataList
    }


    private fun initLinearLayout() {
        with(binding) {
            addView(
                llCurrentTractor,
                tvDescriptionCurrentTractor,
                R.layout.field_layout_current_tractor
            )
            addView(
                llOptionalTractor,
                tvDescriptionOptionalTractor,
                R.layout.field_layout_optional_tractor
            )
        }
    }
}