package hr.trends.ilaktasic.googleTrendsGame.model

import android.os.Parcelable

import kotlinx.android.parcel.Parcelize

@Parcelize
data class TransferModel (val rounds: Int? = null, var currentRound: Int = 0, val players: MutableList<Player> = mutableListOf()) : Parcelable {
    /*constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            mutableListOf<Player>(),
            parcel.readList(mutableListOf<Player>(), Player.javaClass.classLoader))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(rounds)
        parcel.writeInt(currentRound)
        parcel.writeTypedList(players)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TransferModel> {
        override fun createFromParcel(parcel: Parcel): TransferModel {
            return TransferModel(parcel)
        }

        override fun newArray(size: Int): Array<TransferModel?> {
            return arrayOfNulls(size)
        }
    }*/
}