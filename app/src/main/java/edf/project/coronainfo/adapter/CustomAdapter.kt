package edf.project.coronainfo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import edf.project.coronainfo.R.layout.list_item
import edf.project.coronainfo.models.Attributes
import kotlinx.android.synthetic.main.list_item.view.*

class CustomAdapter(private val context: Context, private val items: List<Attributes>)
    : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(list_item, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position])
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bindItem(items: Attributes) {
            containerView.tvProvinsi.text = items.Provinsi
            containerView.tvPositif.text = ""+items.Kasus_Posi
            containerView.tvSembuh.text = ""+items.Kasus_Semb
            containerView.tvMeninggal.text = ""+items.Kasus_Meni
        }
    }

}

