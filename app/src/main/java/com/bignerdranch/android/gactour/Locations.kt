package com.bignerdranch.android.gactour

/*  Data class to hold real-world coordinates of all relevant landmarks */

data class LocationData(
    val name: String,
    val longitude: Double,
    val latitude: Double,
    val distance: Float
)

object LocationProvider {
    val locations = listOf(
        LocationData("Tornado", -93.972247, 44.323026, 50f),
        LocationData("Tennis", -93.972493, 44.329580, 50f),
        LocationData("Beck", -93.97309072033401, 44.32398138197456, 50f),
        LocationData("Nobel", -93.97277916957361, 44.32207370488806, 50f),
        LocationData("Olin", -93.97341709197065, 44.322791458399685, 50f),
        LocationData("Threeflags", -93.969900, 44.324488, 50f),
        LocationData("Arb", -93.975110, 44.320171, 50f),
        LocationData("Confer", -93.97222545473699, 44.32096075723914, 50f),
        LocationData("Anderson", -93.97163849136675, 44.32172925849159, 50f),
        LocationData("ChristChapel", -93.97159732541957, 44.32259886505901, 50f),
        LocationData("FineArts", -93.97408429683793, 44.32164423468588, 50f),
        LocationData("Bjorling", -93.97477534253665, 44.32108392411166, 50f),
        LocationData("Pittman", -93.97306261917961, 44.31952994192993, 50f),
        LocationData("Sohre", -93.97219067125783, 44.31996576206428, 50f),
        LocationData("CICE", -93.97416447897544, 44.32303369944778, 50f),
        LocationData("Swest", -93.9753072379803, 44.3227293943836, 50f),
        LocationData("Library", -93.9722160973431, 44.323567237684486, 50f),
        LocationData("Dining", -93.97050050096162, 44.32434189684726, 50f),
        LocationData("Alumni Hall", -93.97134454025874, 44.32361054337562, 50f),
        LocationData("Registrar", -93.97019127611723, 44.32345358704829, 50f),
        LocationData("Uhler", -93.96947529957478, 44.32384364625235, 50f),
        LocationData("Old Main", -93.97065742474152, 44.32294977256615, 50f),
        LocationData("Rundstrom", -93.96970772074047, 44.32187536584667, 50f),
        LocationData("Norelius", -93.96935143900694, 44.32639453679099, 50f),
        LocationData("Complex", -93.96802268359949, 44.32416143834033, 50f),
        LocationData("Lund", -93.9720971879397, 44.32511367157174, 50f),
        LocationData("CollegeView", -93.97252793591602, 44.327533202774504, 50f),
        LocationData("Arbor View", -93.97782261285086, 44.318635663591394, 50f),
        LocationData("Chapel View", -93.97893089840885, 44.331022902796995, 50f)
    )
}
