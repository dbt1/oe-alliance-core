SUMMARY = "Linux kernel for ${MACHINE}"
LICENSE = "GPL-2.0-only"
SECTION = "kernel"
KV = "3.13.5"

COMPATIBLE_MACHINE = "^(vuduo2|vusolo2|vusolose|vuzero)$"

RPROVIDES:kernel-image = "kernel-${KERNEL_IMAGETYPE}"

inherit kernel machine_kernel_pr

MACHINE_KERNEL_PR:append = "oea4.3-r5"

SRC_URI[md5sum] = "19e9956653437b99b4fa6ec3e16a3e99"
SRC_URI[sha256sum] = "ef7fb307582ff243aacff8a13025fe028634aaf650ada309991ae03622962f61"

LIC_FILES_CHKSUM = "file://${WORKDIR}/linux/COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

KERNEL_CONFIG = "${@bb.utils.contains("MACHINE_FEATURES", "dvbproxy", "defconfig_proxy", "defconfig", d)}"

SRC_URI = "https://source.mynonpublic.com/vuplus/release/kernel/stblinux-${KV}.tar.bz2 \
    file://${KERNEL_CONFIG} \
    file://kernel-add-support-for-gcc5.patch \
    file://kernel-add-support-for-gcc6.patch \
    file://kernel-add-support-for-gcc7.patch \
    file://kernel-add-support-for-gcc8.patch \
    file://kernel-add-support-for-gcc9.patch \
    file://kernel-add-support-for-gcc10.patch \
    file://kernel-add-support-for-gcc11.patch \
    file://kernel-add-support-for-gcc12.patch \
    file://kernel-add-support-for-gcc13.patch \
    file://build-with-gcc12-fixes.patch \
    file://rt2800usb_fix_warn_tx_status_timeout_to_dbg.patch \
    file://add-dmx-source-timecode.patch \
    file://af9015-output-full-range-SNR.patch \
    file://af9033-output-full-range-SNR.patch \
    file://as102-adjust-signal-strength-report.patch \
    file://as102-scale-MER-to-full-range.patch \
    file://cxd2820r-output-full-range-SNR.patch \
    file://dvb-usb-dib0700-disable-sleep.patch \
    file://dvb_usb_disable_rc_polling.patch \
    file://it913x-switch-off-PID-filter-by-default.patch \
    file://tda18271-advertise-supported-delsys.patch \
    file://mxl5007t-add-no_probe-and-no_reset-parameters.patch \
    file://linux-tcp_output.patch \
    file://linux-3.13-gcc-4.9.3-build-error-fixed.patch \
    file://rtl8712-fix-warnings.patch \
    file://0001-Support-TBS-USB-drivers-3.13.patch \
    file://0001-STV-Add-PLS-support.patch \
    file://0001-STV-Add-SNR-Signal-report-parameters.patch \
    file://0001-stv090x-optimized-TS-sync-control.patch \
    file://0002-cp1emu-do-not-use-bools-for-arithmetic.patch \
    file://0003-log2-give-up-on-gcc-constant-optimizations.patch \
    file://blindscan2.patch \
    ${@bb.utils.contains("MACHINE_FEATURES", "dvbproxy", "file://linux_dvb_adapter.patch;patch=1;pnum=1", "", d)} \
    file://genksyms_fix_typeof_handling.patch \
    file://test.patch \
    file://0001-tuners-tda18273-silicon-tuner-driver.patch \
    file://T220-kern-13.patch \
    file://01-10-si2157-Silicon-Labs-Si2157-silicon-tuner-driver.patch \
    file://02-10-si2168-Silicon-Labs-Si2168-DVB-T-T2-C-demod-driver.patch \
    file://CONFIG_DVB_SP2.patch \
    file://dvbsky.patch \
    file://move-default-dialect-to-SMB3.patch \
    file://linux3.4-ARM-8933-1-replace-Sun-Solaris-style-flag-on-section.patch \
    file://fix-build-with-binutils-2.41.patch \
    "

SRC_URI:append:vuduo2 = " file://brcm_s3_wol.patch;patch=1;pnum=1 "
SRC_URI:append:vusolose = " file://brcm_s3_wol.patch;patch=1;pnum=1 \
                          file://linux_mtd_bbt_maxblock.patch"

SRC_URI:append:vusolo2 = " file://linux-bcm_ethernet.patch;patch=1;pnum=1 "

SRC_URI:append:vuzero = " file://linux_nand_bcm.patch "

S = "${WORKDIR}/linux"
B = "${WORKDIR}/build"

export OS = "Linux"
KERNEL_OBJECT_SUFFIX = "ko"
KERNEL_OUTPUT = "vmlinux"
KERNEL_IMAGETYPE = "vmlinux"
KERNEL_IMAGEDEST = "tmp"

KERNEL_EXTRA_ARGS = "EXTRA_CFLAGS=-Wno-attribute-alias"

FILES:${KERNEL_PACKAGE_NAME}-image = "/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz"

do_configure:prepend() {
    if [ -e ${WORKDIR}/defconfig_proxy ]; then
    mv ${WORKDIR}/defconfig_proxy ${WORKDIR}/defconfig
    fi
}

kernel_do_install:append() {
    ${STRIP} ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
    gzip -9c ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} > ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
    rm ${D}/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
}

pkg_postinst:kernel-image () {
    if [ "x$D" == "x" ]; then
        if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz ] ; then
            flash_erase  /dev/${MTD_KERNEL} 0 0
            nandwrite -p /dev/${MTD_KERNEL} /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
            rm -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}.gz
        fi
    fi
    true
}

do_rm_work() {
}

# extra tasks
addtask kernel_link_images after do_compile before do_install
