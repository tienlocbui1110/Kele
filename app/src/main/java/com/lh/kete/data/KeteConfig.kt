package com.lh.kete.data

/**
 * Created by Tien Loc Bui on 18/03/2019.
 */
class KeteConfig {

    val version: Int
    val buttonNumber: Int
    val buttonConfig: Array<ButtonConfig>

    constructor(
        version: Int = 0,
        buttonNumber: Int = 0,
        buttonConfig: Array<ButtonConfig>
    ) {
        this.version = version
        this.buttonNumber = buttonNumber
        this.buttonConfig = buttonConfig
    }
}